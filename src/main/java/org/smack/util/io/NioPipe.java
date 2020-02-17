package org.smack.util.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;

/**
 * A simple pipe.  Write to the write end, read from the read end.
 *
 * @version $Rev: 305 $
 * @author Michael Binz
 */
public final class NioPipe
{
    private static final int BUFFER_SIZE = 10 * 1024;

    private final Pipe _pipe;

    /**
     * Create an instance.
     *
     * @param bufferSize The size of the pipe's internal buffer.
     */
    public NioPipe( int bufferSize )
    {
        try
        {
            _pipe = Pipe.open();
        }
        catch ( IOException e )
        {
            throw new InternalError( e.toString() );
        }
    }

    /**
     * Create an instance with a 10k buffer size.
     */
    public NioPipe()
    {
        this( BUFFER_SIZE );
    }

    /**
     * Get the Pipe's write end.
     *
     * @return The write end.
     */
    public OutputStream getWriteEnd()
    {
        return Channels.newOutputStream( _pipe.sink() );
    }

    /**
     * Get the pipes read end.
     *
     * @return The read end.
     */
    public InputStream getReadEnd()
    {
        return Channels.newInputStream( _pipe.source() );
    }

    /**
     * Close the pipe.
     */
    public void close()
    {
        forceClose( _pipe.sink() );
        forceClose( _pipe.source() );
    }

    private void forceClose( Closeable closeable )
    {
        try
        {
            closeable.close();
        }
        catch ( Exception ignore )
        {

        }
    }
}
