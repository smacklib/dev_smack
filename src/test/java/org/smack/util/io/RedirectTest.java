package org.smack.util.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.smack.util.StringUtil;

public class RedirectTest
{
    @Test
    public void testStdout() throws IOException
    {
        try ( Redirect stdOut = new Redirect( Redirect.StdStream.out ) )
        {
            System.out.println( "micbinz" );
            System.out.println();
            System.out.println( "einstein" );

            var lines = stdOut.content();

            assertEquals( 3, lines.size() );
            assertEquals( "micbinz", lines.get( 0 ) );
            assertEquals( StringUtil.EMPTY_STRING, lines.get( 1 ) );
            assertEquals( "einstein", lines.get( 2 ) );
        }
    }

    @Test
    public void testStderr() throws IOException
    {
        try ( Redirect stdErr = new Redirect( Redirect.StdStream.err ) )
        {
            System.err.println( "micbinz" );
            System.err.println();
            System.err.println( "einstein" );

            var lines = stdErr.content();

            assertEquals( 3, lines.size() );
            assertEquals( "micbinz", lines.get( 0 ) );
            assertEquals( StringUtil.EMPTY_STRING, lines.get( 1 ) );
            assertEquals( "einstein", lines.get( 2 ) );
        }
    }
}
