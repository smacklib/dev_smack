package org.smack.util.xml;

import java.io.IOException;
import java.io.InputStream;

public class CheckableInputStream extends InputStream
{
    private final InputStream _delegate;
    private boolean _isClosed;

    CheckableInputStream( InputStream delegate )
    {
        _delegate = delegate;
    }

    @Override
    public void close() throws IOException
    {
        _isClosed = true;
        _delegate.close();
    }

    boolean isClosed()
    {
        return _isClosed;
    }

    @Override
    public int read() throws IOException
    {
        return _delegate.read();
    }
    @Override
    public int read( byte[] b ) throws IOException
    {
        return _delegate.read( b );
    }
    @Override
    public int read( byte[] b, int off, int len ) throws IOException
    {
        return _delegate.read( b, off, len );
    }
}
