/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022-2026 Michael G. Binz
 */
package org.smack.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import org.smack.util.JavaUtil.SupplierX;

/**
 * Trampoline-based continuation-passing infrastructure used to evaluate
 * Scheme expressions without growing the Java call stack, and to
 * implement Scheme's {@code call-with-current-continuation} and
 * {@code with-exception-handler}/{@code raise} (r7rs 6.10, 6.11).
 * <p>
 * Computations are expressed as chains of {@link Thunk}s. {@link #toStack}
 * repeatedly runs a thunk chain to completion ({@link #trampoline}) and
 * returns the final result of type {@code T}. Exceptions of the declared
 * type {@code X} that occur while a chain is running are not propagated
 * natively; instead they are redirected to whichever {@link Cont} is
 * currently on top of the handler stack maintained by
 * {@link #pushExceptionHandler} / {@link #popExceptionHandler}, which is
 * how {@code with-exception-handler} is implemented. Pushes and pops of
 * that handler stack must be balanced by the caller; an unmatched pop
 * fails fast (see {@link #popExceptionHandler}).
 * <p>
 * A single instance is designed to be reused across many, possibly
 * nested or sequential, {@link #toStack} calls (see {@code ScreamEvaluator}
 * use through {@code evalFco}/{@code load}). This is essential for
 * escaping ("full", non-delimited) continuations captured via
 * {@code call-with-current-continuation}: such a continuation can be
 * invoked from a later, unrelated {@code toStack()} call and must still
 * be able to deliver its result to whichever call is running at that
 * moment, which is why {@link #_result} is a shared instance field rather
 * than call-local state (see its Javadoc for the concrete failure mode of
 * "fixing" that). A consequence is that instances are <b>not
 * thread-safe</b> and must be confined to a single thread; concurrent use
 * of one instance from multiple threads is unsupported.
 *
 * @param <T> The overall result type produced by a completed thunk chain.
 * @param <X> The exception type intercepted and redirected to the
 * exception handler stack; other exceptions propagate natively.
 *
 * @author micbinz
 */
public class Continuation<T, X extends Exception>
{
    private long _thunkCount;

    private final Class<X> _exceptionClass;

    /**
     * Will receive the result for all continuation invocations.
     * <p>
     * This MUST be a shared instance field and must NOT be replaced by a
     * variable local to {@link #toStack(ToStackOp, Cont)}. Scheme's
     * {@code call-with-current-continuation} can capture a continuation in
     * one {@code toStack()} call and invoke it later, from a completely
     * different, later {@code toStack()} call on this same instance (see
     * {@code de.michab.scream.fcos.Continuation}, and the test
     * {@code R7rs_6_10_Control_features_Test#call_cc_restart_2}). Invoking
     * such an escaped continuation discards the later call's own
     * continuation chain and resumes the original, captured chain instead,
     * which bottoms out in the exit continuation created inline by
     * {@link #toStack(ToStackOp, Cont)}, belonging to the original,
     * by-then-already-returned call. For that exit continuation to still
     * be able to deliver its result to whichever
     * {@code toStack()} call is currently running its trampoline, the slot
     * it writes to has to be shared across calls, not call-local. A
     * call-local {@code Holder} looks like a safer, more "correct"
     * alternative but silently breaks cross-call/escaping continuations.
     */
    private final Holder<T> _result = new Holder<>();

    /**
     * Create an instance.
     *
     * @param exceptionClass The class of the handled exception type.
     */
    public Continuation( Class<X>  exceptionClass )
    {
        _exceptionClass = exceptionClass;
    }

    @FunctionalInterface
    public static interface ToStackOp<T> {
        Thunk call( Cont<T> c )
            throws Exception;
    }

    @FunctionalInterface
    public static interface Cont<R> {
        Thunk accept(R result);
    }

    @FunctionalInterface
    public static interface Thunk {
        Thunk run() throws Exception;
    }

    private Deque<Cont<X>> _exceptionHandlers = new ArrayDeque<>();

    /**
     * Runs the passed thunk chain to completion, redirecting exceptions of
     * type {@code X} to the exception handler currently on top of
     * {@link #_exceptionHandlers}.
     *
     * @param t The thunk to execute.
     * @throws Exception If an exception not assignable to {@code X} happens,
     * or if the exception handler invoked for an {@code X} itself throws.
     */
    private void trampoline(
            Thunk t )
                    throws Exception
    {
        while ( t != null )
        {
            _thunkCount++;

            try
            {
                t = t.run();
            }
            catch ( Exception e )
            {
                // If the exception is of type xClass ...
                if ( _exceptionClass.isAssignableFrom( e.getClass() ) )
                    // ... then the passed exception continuation is called.
                    // If this in turn throws an exception then thunk processing
                    // is terminated.
                    t = _exceptionHandlers.peek().accept( _exceptionClass.cast( e ) );
                else
                    // Otherwise terminate.
                    throw e;
            }
        }
    }

    /**
     * @return The current value of the thunk counter. Used
     * in testing.
     */
    public long thunkCount()
    {
        return _thunkCount;
    }

    /**
     * Set the thunk counter.  Used in debugging.
     *
     * @param newValue The new value for the thunk counter.
     */
    public void thunkCount( long newValue )
    {
        _thunkCount = newValue;
    }

    /**
     * Execute a continuation-based operation with an exception handler and
     * return the result on the stack.
     *
     * @param <T> The result type.
     * @param <X> The type of the exception that is handled.
     * @param op The operation to execute.
     * @param exceptionHandler The exception handler.  Must not
     * be {@code null}.
     * @param exceptionClass The type of the exception that is handled.
     * @return The result of the passed operation.
     * @throws Exception
     */
    public
    T toStack(
            ToStackOp<T> op,
            Cont<X> exceptionHandler )
        throws Exception
    {
        Objects.requireNonNull( exceptionHandler );

        _result.set( null );

        try
        {
            pushExceptionHandler( exceptionHandler );

            // Note: op.call(...) itself runs here, outside of trampoline()'s
            // catch. A synchronous throw from it (before it returns a Thunk)
            // is therefore not redirected through the handler stack above,
            // unlike every later step of the resulting chain. This is
            // harmless today only because the handler just pushed above is
            // already the one that would apply. Do not "fix" this by routing
            // it through the same catch -- re-entering that catch for the
            // very exception it just caught would loop forever.
            trampoline(
                    op.call( s -> { _result.set( s ); return null; } ) );

            return _result.get();
        }
        finally
        {
            popExceptionHandler();
        }
    }

    /**
     * Execute a continuation-based operation and
     * return the result on the stack.
     *
     * @param <T> The result type.
     * @param op The operation to execute.
     * @return The result of the passed operation.
     * @throws Exception
     */
    public
    T toStack( ToStackOp<T> op )
        throws Exception
    {
        Holder<X> exception = new Holder<>();

        Cont<X> handler =
                ae -> {
                    exception.set( ae );
                    return null;
                };

        toStack( op, handler );

        if ( exception.get() != null )
            throw exception.get();

        return _result.get();
    }

    public <R> Thunk toCont( SupplierX<R> supplier, Cont<R> c )
    {
        return () -> c.accept( supplier.get() );
    }

    public void pushExceptionHandler( Cont<X> handler )
    {
        _exceptionHandlers.push(
                Objects.requireNonNull( handler ) );
    }

    /**
     * Removes and returns the exception handler currently on top of the
     * handler stack.
     *
     * @return The removed handler.
     * @throws IllegalStateException If called without a matching prior
     * {@link #pushExceptionHandler(Cont)}, i.e. the handler stack is empty.
     * This guards against a confusing raw {@code NoSuchElementException}
     * from an unbalanced push/pop in caller code.
     */
    public Cont<X> popExceptionHandler()
    {
        if ( _exceptionHandlers.isEmpty() )
            throw new IllegalStateException(
                    "popExceptionHandler() called without a matching pushExceptionHandler()." );

        return _exceptionHandlers.pop();
    }
}
