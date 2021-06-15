/*
 * $Id$
 *
 * Smack io
 *
 * Copyright © 2021 Michael G. Binz
 */
package org.smack.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.smack.util.FileUtil;

/**
 * Redirect a standard stream.
 *
 * @author Michael Binz
 */
public class Redirect implements Closeable
{
    private static Logger LOG = Logger.getLogger( Redirect.class.getName() );

    /**
     * The stream types that can be redirected.
     */
    public enum StdStream { out, err };

    private boolean _closed;

    private final PrintStream _originalStream;
    private final StdStream _selector;

    private final ByteArrayOutputStream _outBuffer =
            new ByteArrayOutputStream();
    private final PrintStream _outBufferPs =
            new PrintStream( _outBuffer );

    /**
     * Redirect the passed stream type.
     *
     * @param streamSelector The stream type to redirect.
     */
    public Redirect( StdStream streamSelector )
    {
        _selector = streamSelector;

        if ( _selector == StdStream.out )
        {
            _originalStream = System.out;
            System.setOut( _outBufferPs );
        }
        else if ( _selector == StdStream.err )
        {
            _originalStream = System.err;
            System.setErr( _outBufferPs );
        }
        else
            throw new IllegalArgumentException();
    }

    /**
     * @return The raw bytes that were received.
     */
    public byte[] contentRaw()
    {
        _outBufferPs.flush();

        return _outBuffer.toByteArray();
    }

    /**
     * @return The received data, properly translated to lines. Empty
     * lines are contained in the list as empty strings.
     */
    public List<String> content()
    {
        try ( var bis =
                new ByteArrayInputStream( contentRaw() ) )
        {
            return FileUtil.readLines( bis );
        }
        catch ( Exception e )
        {
            LOG.warning( "Unexpected: " + e.getMessage() );
            return Collections.emptyList();
        }
    }

    /**
     * Restore the original stream setup.
     */
    @Override
    public void close()
    {
        if ( _closed )
            return;
        _closed = true;

        if ( _selector == StdStream.out )
        {
            if ( System.out != _outBufferPs )
                LOG.warning( "ConcurrentModification of System.out." );
            System.setOut( _originalStream );
        }
        else if ( _selector == StdStream.err )
        {
            if ( System.err != _outBufferPs )
                LOG.warning( "ConcurrentModification of System.err." );
            System.setErr( _originalStream );
        }
    }
}
