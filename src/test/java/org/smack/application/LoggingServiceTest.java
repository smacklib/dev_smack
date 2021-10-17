package org.smack.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;
import org.smack.util.FileUtil;
import org.smack.util.collections.CollectionUtil;
import org.smack.util.io.Redirect;

public class LoggingServiceTest
{
    @Test
    public void plain() throws IOException
    {
        LoggingService ls =
                new LoggingService( "smacktest", getClass().getSimpleName() );

        assertTrue( ls.getLogDir().exists() );
        FileUtil.delete( ls.getLogDir() );
        assertFalse( ls.getLogDir().exists() );

        ls =
                new LoggingService( "smacktest", getClass().getSimpleName() );
        assertTrue( ls.getLogDir().exists() );
    }

    @Test
    public void logoutput() throws IOException
    {
        LoggingService ls =
                new LoggingService( "smacktest", getClass().getSimpleName() );
        var log = Logger.getLogger( getClass().getName() );

        try ( var redir = new Redirect( Redirect.StdStream.err ) )
        {
            log.severe( "severe" );
            var errout = redir.content();
            assertEquals( "SEVERE: severe", CollectionUtil.lastElement( errout ).get() );
            log.warning( "warning" );
            errout = redir.content();
            assertEquals( "WARNING: warning", CollectionUtil.lastElement( errout ).get() );
        }
    }
}
