/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2021 Michael G. Binz
 */
package org.smack.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.Test;
import org.smack.util.FileUtil;
import org.smack.util.collections.CollectionUtil;
import org.smack.util.io.Redirect;

public class LoggingServiceTest
{
    private void clearDirectory( File d )
    {
        assertTrue( d.isDirectory() );

        for ( var c : d.listFiles() )
            assertTrue( c.delete() );

        assertEquals( 0, d.list().length );
    }

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
    public void logOutputStderr() throws IOException
    {
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
            log.info( "info" );
            errout = redir.content();
            // info messages are not printed to stderr.
            assertTrue( errout.isEmpty() );
        }
    }

    @Test
    public void logOutputFile() throws IOException
    {
        LoggingService ls =
                new LoggingService( "smacktest", getClass().getSimpleName() );
        clearDirectory( ls.getLogDir() );

        var log = Logger.getLogger( getClass().getName() );

        // Prevent stderr from spam.
        try ( var redir = new Redirect( Redirect.StdStream.err ) )
        {
            log.severe( "severe" );
            log.warning( "warning" );
            log.info( "info" );
        }

        List<File> logfiles = Arrays.stream(
                ls.getLogDir().listFiles() ).filter(
                        e -> e.getName().endsWith( ".log" ) ).collect(
                                Collectors.toList() );

        assertEquals( 1, logfiles.size() );

        HashSet<String> lines = new HashSet<String>();
        FileUtil.readLines( logfiles.get( 0 ) ).forEach( lines::add );

        assertTrue( lines.contains( "SEVERE: severe" ) );
        assertTrue( lines.contains( "WARNING: warning" ) );
        assertTrue( lines.contains( "INFO: info" ) );
    }
}
