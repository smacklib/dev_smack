/* $Id$
 *
 * Utilities
 *
 * Copyright © 2021 Michael G. Binz
 */
package org.smack.util;

/**
 * An exception that allows to pass a formatted message.
 *
 * @author micbinz
 */
@SuppressWarnings("serial")
public class FormattedEx extends Exception
{
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public FormattedEx( String message )
    {
        super( message );
    }

    /**
     * Creates a formatted message.
     *
     * @param message The format specifier.
     * @param args The format arguments
     */
    public FormattedEx( String message, Object... args )
    {
        super( String.format( message, args ) );
    }

    /**
     * Creates a formatted message and a cause.
     *
     * @param cause The cause of this exception.
     * @param message The format specifier.
     * @param args The format arguments
     */
    public FormattedEx( Throwable cause, String message, Object... args )
    {
        super( String.format( message, args ), cause );
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     */
    public FormattedEx( Throwable cause, String message )
    {
        super( message, cause );
    }

}
