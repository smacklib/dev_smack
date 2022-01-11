/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2011-2022 Michael G. Binz
 */
package org.smack.util;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * General utilities.
 *
 * @author Michael Binz
 */
public class JavaUtil
{
    private static final Logger LOG =
            Logger.getLogger( JavaUtil.class.getName() );

    private static class AssertionException extends RuntimeException
    {
        private static final long serialVersionUID = -2923329590438227638L;

        public AssertionException()
        {
        }
        public AssertionException( String msg )
        {
            super( msg );
        }
    }

    private JavaUtil()
    {
        throw new AssertionException();
    }

    public static void Assert( boolean condition, String message )
    {
        if ( condition )
            return;
        throw new AssertionException( message );
    }

    /**
     * Performs an Assertion with a formatted exception in case the
     * assertion does not hold.
     *
     * @param condition A condition that is asserted to return true.
     * @param message A format expression.
     * @param args Format arguments.
     */
    public static void Assert(
            boolean condition,
            String format,
            Object ... args )
    {
        if ( condition )
            return;
        throw new AssertionException(
                String.format( format, args ) );
    }

    public static void Assert( boolean condition )
    {
        if ( condition )
            return;
        throw new AssertionException();
    }

    /**
     * Sleep for an amount of milliseconds or until interrupted.
     * @param millis The time to sleep.
     */
    public static void sleep( long millis )
    {
        try
        {
            Thread.sleep( millis );
        }
        catch ( InterruptedException ignore )
        {
        }
    }

    /**
     * A parameterless operation throwing an exception.
     */
    public interface Fx
    {
        void call()
            throws Exception;
    }

    /**
     * Calls the passed operation, ignoring an exception.
     * @param operation The operation to call.
     */
    public static void force( Fx operation )
    {
        try
        {
            operation.call();
        }
        catch ( Exception e )
        {
            LOG.log( Level.INFO, "Force exception.", e );
        }
    }

    /**
     * Test if an array is empty.
     *
     * @param <T> The array type.
     * @param array The array to test. {@code null} is allowed.
     * @return {@code true} if the array is {@code null} or has a length
     * greater than zero.
     */
    public static <T> boolean isEmptyArray( T[] array )
    {
        return array == null || array.length == 0;
    }

    /**
     * Create an exception with a formatted message.
     *
     * @param fmt The format string.
     * @param args The arguments.
     * @return The newly created exception.
     */
    public static Exception fmtX( String fmt, Object... args )
    {
        return new Exception(
                String.format( fmt, args ) );
    }

    /**
     * Create an exception with a formatted message. Allows to
     * add a cause.
     *
     * @param cause The cause of the created exception.
     * @param fmt The format string.
     * @param args The arguments.
     * @return The newly created exception.
     */
    public static Exception fmtX(
            Throwable cause,
            String fmt,
            Object... args )
    {
        return new Exception(
                String.format( fmt, args ),
                cause );
    }

    /**
     * Initialize and return an object.
     *
     * <pre>
     * private static JTextArea ghEditWnd = init(
     *   new JTextArea(),
     *   n -> {
     *     n.setEditable(
     *            false );
     *     n.setFont( new Font(
     *            Font.MONOSPACED,
     *            n.getFont().getStyle(),
     *            n.getFont().getSize() ) );
     *     return n;
     *  } );
     * </pre>
     *
     * @param <T> The object type.
     * @param t The initial instance.
     * @param makeit A function that initializes the object.
     * @return The initialized instance.
     */
    public static <T> T init( T t, Function<T, T> makeit )
    {
        return makeit.apply( t );
    }
}
