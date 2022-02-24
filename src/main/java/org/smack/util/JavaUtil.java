/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2011-2022 Michael G. Binz
 */
package org.smack.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
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
        throw new AssertionError();
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
    @FunctionalInterface
    public interface Fx
    {
        void call()
            throws Exception;
    }

    @FunctionalInterface
    public interface SupplierX<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Exception;
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
     * of zero.
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
     * Initialize and return an object in a closed scope.
     *
     * <pre>
     * private static JTextArea ghEditWnd = make(
     *   () -> {
     *     var n = new JTextArea(),
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
     * @param makeit A supplier that creates the object.
     * @return The initialized instance.
     * @see JavaUtil#makex(SupplierX)
     */
    public static <T> T make( Supplier<T> makeit )
    {
        return makeit.get();
    }

    /**
     * Initialize and return an object in a closed scope. Use if
     * the initialization may throw exceptions.
     *
     * @param <T> The object type.
     * @param t The initial instance.
     * @param makeit A supplier that creates the object.
     * @return The initialized instance.
     * @see #make(Supplier)
     */
    public static <T> T makex( SupplierX<T> makeit ) throws Exception
    {
        return makeit.get();
    }

    private static <T> T toRtx( SupplierX<T> s )
    {
        try
        {
            return s.get();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Execute the passed command line.
     *
     * @param stdOut The lines received on stdout.  {@code null} is allowed.
     * @param stdErr The lines received on stderr.  {@code null} is allowed.
     * @param argv The command to be executed, one parameter per slot.  An
     * empty array is not allowed.
     * @return The return code of the command. Per convention a zero means
     * success.
     * @throws InterruptedException If command execution got interrupted.
     * @throws {@link RuntimeException} In case of errors.
     */
    public static int exec(
            List<String> stdOut,
            List<String> stdErr,
            String ... argv ) throws InterruptedException
    {
        Assert( ! isEmptyArray( argv ) );

        try ( Disposer d = new Disposer() )
        {
            var process = Runtime.getRuntime().exec(
                    argv );

            // See http://grep.codeconsult.ch/2005/02/01/better-cleanup-your-process-objects/
            var out = d.register(
                    process.getInputStream() );
            var err = d.register(
                    process.getErrorStream() );
            d.register(
                    process.getOutputStream() );


            // Ensure that the output channels are unconditionally read, since
            // this is required in some cases to not block the executed
            // command.  Technically this needs to be done in the background.
            List<String> outHolder = new ArrayList<String>();

            new Thread( () -> toRtx( () ->
            {
                return outHolder.addAll( FileUtil.readLines( out ) );
            } ) ).start();

            List<String> errHolder = new ArrayList<String>();

            new Thread( () -> toRtx( () ->
                {
                    return errHolder.addAll( FileUtil.readLines( err ) );
                } ) ).start();

            // Finally let the started process do its work.  Make sure that
            // the resulting data is taken-over *after* the process finished.

            try {
                return process.waitFor();
            }
            finally
            {
                if ( stdOut != null )
                    stdOut.addAll( outHolder );
                if ( stdErr != null )
                    stdErr.addAll( errHolder );
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
