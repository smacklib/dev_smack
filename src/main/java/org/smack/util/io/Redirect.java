/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2021 Michael G. Binz
 */
package org.smack.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.PrintStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.smack.util.FileUtil;

/**
 * Redirect a standard stream.
 *
 * @author Michael Binz
 */
public class Redirect implements Closeable
{
    /**
     * @return A redirector corresponding to stdout.
     */
    public static Redirect out()
    {
        return new Redirect( StdStream.out );
    }

    /**
     * @return A redirector corresponding to stderr.
     */
    public static Redirect err()
    {
        return new Redirect( StdStream.err );
    }

    /**
     * The stream types that can be redirected.
     */
    public enum StdStream {
        out( () -> { return System.out; }, System::setOut ),
        err( () -> { return System.err; }, System::setErr );

        StdStream( Supplier<PrintStream> s, Consumer<PrintStream> c )
        {
            _get = s;
            _set = c;
        }

        /**
         * Replaces the encapsulated standard stream by the passed stream.
         * @param p The stream to be used instead of the encapsulated standard
         * stream.
         * @return The stream that was replaced.
         */
        public PrintStream replace( PrintStream p )
        {
            try {
                return get();
            }
            finally {
                _set.accept( p );
            }
        }

        /**
         * @return The currently set output stream.
         */
        public PrintStream get() {
            return _get.get();
        }

        private final Consumer<PrintStream> _set;
        private final Supplier<PrintStream> _get;
    };

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

        _originalStream = streamSelector.replace( _outBufferPs );
    }

    /**
     * @return The raw bytes that were received.  Note that this operation
     * resets the content.
     */
    public byte[] contentRaw()
    {
        _outBufferPs.flush();

        try {
            return _outBuffer.toByteArray();
        }
        finally {
            _outBuffer.reset();
        }
    }

    /**
     * @return The received data, properly translated to lines. Empty
     * lines are contained in the list as empty strings.  The operation
     * resets the content.
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
            throw new AssertionError( "Unexpected.", e );
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

        if ( _selector.replace( _originalStream ) !=  _outBufferPs )
            throw new Error( "ConcurrentModification : " + _selector );
    }
}
