/*
 * Copyright © 2020 Michael Binz.
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.NoSuchFileException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * Test file utilities.
 */
public class FileUtilTest
{
    private String concat( String ... lines )
    {
        return StringUtil.concatenate(
                StringUtil.EOL,
                lines );
    }

    @Test
    public void testReadLines() throws Exception
    {
        var lines =
                new String[]
                        {
                                "1",
                                "2",
                                "3",
                                "4",
                                StringUtil.EMPTY_STRING
                        };
        var toTrim =
                concat( lines );
        var sr =
                new StringReader( toTrim );
        var list =
                FileUtil.readLines( sr );
        assertEquals( lines.length-1, list.size() );

        // Assert that the reader got closed.
        try
        {
            sr.read();
            assertTrue( false );
        }
        catch (Exception expected)
        {
        }
    }

    @Test
    public void testReadLines2() throws Exception
    {
        var lines =
                new String[]
                        {
                                "1",
                                "2",
                                "3",
                                "4",
                                StringUtil.EMPTY_STRING,
                                StringUtil.EMPTY_STRING
                        };
        var toTrim =
                concat( lines );
        var sr =
                new StringReader( toTrim );

        var list =
                FileUtil.readLines( sr );
        assertEquals( 5, list.size() );
        assertEquals(
                StringUtil.EMPTY_STRING,
                list.get( 4 ) );

        // Assert that the reader got closed.
        try
        {
            sr.read();
            assertTrue( false );
        }
        catch (Exception expected)
        {
        }
    }

    @Test
    public void testReadLinesBa() throws Exception
    {
        var lines =
                new String[]
                        {
                                "1",
                                "2",
                                "3",
                                "4",
                                StringUtil.EMPTY_STRING
                        };
        var toTrim =
                concat( lines );
        boolean isClosed[] =
                new boolean[] {false};
        var sr =
                new ByteArrayInputStream( toTrim.getBytes() )
        {
            @Override
            public
            void close()
            {
                isClosed[0] = true;
            }
        };

        assertFalse( isClosed[0] );
        var list =
                FileUtil.readLines( sr );
        assertEquals(
                lines.length-1,
                list.size() );
        assertTrue( isClosed[0] );
    }

    @Test
    public void testReadLinesFile() throws Exception
    {
        File testFile = File.createTempFile( "smack", null );

        final var expectedLineEnd = ":marker";

        try ( FileWriter w = new FileWriter( testFile ) )
        {
            for ( int i = 0 ; i < 1000 ; i++ )
                w.write( i + " : " + testFile.getPath() + expectedLineEnd + StringUtil.EOL );
        }

        assertTrue( testFile.exists() );

        try
        {
            var list =
                    FileUtil.readLines( testFile );
            for ( var c : list )
                assertTrue( c.endsWith( expectedLineEnd ) );
            assertEquals( 1000, list.size() );
        }
        finally
        {
            assertTrue( testFile.delete() );
        }
    }

    @Test
    public void testReadLinesFileNotExists() throws Exception
    {
        File testFile = new File( UUID.randomUUID().toString() );

        try
        {
            FileUtil.readLines( testFile );
            fail();
        }
        catch ( NoSuchFileException e )
        {
            assertEquals( testFile.getPath(), e.getMessage() );
        }
    }

    @Test
    public void testForceClose() throws Exception
    {
        // Prevent JavaUtil.force() info log from spamming test output.
        Logger.getLogger( JavaUtil.class.getName() ).setLevel( Level.WARNING );

        File testFile = File.createTempFile( "smack", null );

        var sr =
                new FileInputStream( testFile )
        {
            private int _closedCount = 0;

            @Override
            public
            void close() throws IOException
            {
                if ( _closedCount == 0 )
                    super.close();

                _closedCount++;
                throw new IOException( "Test exception." );
            }
            public int getClosedCount()
            {
                return _closedCount;
            }
        };

        assertEquals( 0, sr.getClosedCount() );
        try
        {
            sr.close();
            fail();
        }
        catch (IOException e) {
            assertEquals( 1, sr.getClosedCount() );
        }

        FileUtil.forceClose( sr );
        assertEquals( 2, sr.getClosedCount() );

        assertTrue( testFile.exists() );
        assertTrue( testFile.delete() );
    }
}
