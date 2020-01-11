/* $Id: e0ac8d41a40bb976b6af69dc098c3a643d70b2e8 $
 *
 * Unpublished work.
 * Copyright © 2018 Michael G. Binz
 */
package org.jdesktop.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logging Utility, replacement for java.util.Logger.  Adds printf-like
 * message formatting.
 *
 * @version $Rev$
 * @author Michael Binz
 * @deprecated No replacement.  Use standard logging.
 */
@Deprecated
public class Log
{
    private final Logger _delegate;

    public Log( Class<?> delegate )
    {
        _delegate = Logger.getLogger( delegate.getName() );
    }

    public void out( Level level, String format, Object ... args )
    {
        if ( ! _delegate.isLoggable( level ) )
            return;

        _delegate.log(
                level,
                String.format( format, args ) );
    }

    public void out( Level level, Throwable exception, String format, Object ... args )
    {
        if ( ! _delegate.isLoggable( level ) )
            return;

        _delegate.log(
                level,
                String.format( format, args ),
                exception );
    }

    public void fine( Throwable exception, String format, Object ... args )
    {
        out( Level.FINE, exception, format, args );
    }
    public void fine( String format, Object ... args )
    {
        out( Level.FINE, format, args );
    }
    public void info( Throwable exception, String format, Object ... args )
    {
        out( Level.INFO, exception, format, args );
    }
    public void info( String format, Object ... args )
    {
        out( Level.INFO, format, args );
    }
    public void warn( Throwable exception, String format, Object ... args )
    {
        out( Level.WARNING, exception, format, args );
    }
    public void warn( String format, Object ... args )
    {
        out( Level.WARNING, format, args );
    }
    public void error( Throwable exception, String format, Object ... args )
    {
        out( Level.SEVERE, exception, format, args );
    }
    public void error( String format, Object ... args )
    {
        out( Level.SEVERE, format, args );
    }

    public Logger getDelegate()
    {
        return _delegate;
    }
}
