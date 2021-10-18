/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-21 Michael G. Binz
 */
package org.smack.util.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.smack.util.StringUtil;

public class RedirectTest
{
    private void common( Redirect.StdStream enm ) throws IOException
    {
        try ( Redirect stdOut = new Redirect( enm ) )
        {
            enm.get().println( "micbinz" );
            enm.get().println();
            enm.get().println( "einstein" );

            var lines = stdOut.content();

            assertEquals( 3, lines.size() );
            assertEquals( "micbinz", lines.get( 0 ) );
            assertEquals( StringUtil.EMPTY_STRING, lines.get( 1 ) );
            assertEquals( "einstein", lines.get( 2 ) );

            lines = stdOut.content();

            assertTrue( lines.isEmpty() );

            enm.get().println( "micbinz" );

            lines = stdOut.content();
            assertEquals( 1, lines.size() );
            assertEquals( "micbinz", lines.get( 0 ) );
        }
    }

    @Test
    public void testStdout() throws IOException
    {
        common( Redirect.StdStream.out );
    }

    @Test
    public void testStderr() throws IOException
    {
        common( Redirect.StdStream.err );
    }
}
