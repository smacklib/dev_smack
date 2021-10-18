/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2020-21 Michael G. Binz
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
     * Creates a formatted message with a cause.
     *
     * @param cause The cause of this exception.
     * @param message The format specifier.
     * @param args The format arguments
     */
    public FormattedEx( Throwable cause, String message, Object... args )
    {
        super( String.format( message, args ), cause );
    }
}
