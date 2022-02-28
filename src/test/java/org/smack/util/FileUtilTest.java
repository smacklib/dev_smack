/*
 * Copyright © 2020 Michael Binz.
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.util.PlatformType;
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
    public void testReadLinesEmptyStream() throws Exception
    {
        var list =
                FileUtil.readLines(
                        new StringReader( StringUtil.EMPTY_STRING ) );
        assertEquals(
                0,
                list.size() );
    }

    @Test
    public void testReadLinesBufferedReader() throws Exception
    {
        var list =
                FileUtil.readLines(
                        new BufferedReader(
                                new StringReader( StringUtil.EMPTY_STRING ) ) );
        assertEquals(
                0,
                list.size() );
    }

    @Test
    public void testReadLinesFile() throws Exception
    {
        File testFile = File.createTempFile( getClass().getSimpleName(), null );

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

        File testFile = File.createTempFile( getClass().getSimpleName(), null );

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

    @Test
    public void testUserHome() throws Exception
    {
        final var userHome = "user.home";

        String originalUserHome = System.getProperty( userHome );
        assertTrue( StringUtil.hasContent( originalUserHome ) );

        File testFile = File.createTempFile( "smack", null );

        File testDir = testFile.getParentFile();

        assertTrue( testDir.isDirectory() );

        try
        {
            System.setProperty( userHome, testDir.getPath() );
            var userHomeDir = FileUtil.getUserHome();
            assertTrue( userHomeDir.equals( testDir ) );

            System.setProperty( userHome, testFile.getPath() );
            try {
                FileUtil.getUserHome();
                fail();
            }
            catch ( AssertionError e )
            {
                // user.home is not a directory.
            }

            System.setProperty( userHome, "doesNotExist" );
            try {
                FileUtil.getUserHome();
                fail();
            }
            catch ( AssertionError e )
            {
                // user.home does not exist.
            }
        }
        finally
        {
            System.setProperty( userHome, originalUserHome );
            assertTrue( testFile.delete() );
        }
    }

    @Test
    public void testDeleteFile() throws Exception
    {
        var f = File.createTempFile( "test_", ".tmp" );

        assertTrue( f.exists() );
        FileUtil.delete( f );
        assertFalse( f.exists() );
    }

    @Test
    public void testDeleteDir() throws Exception
    {
        var dp = Files.createTempDirectory( getClass().getSimpleName() );
        var df = dp.toFile();

        assertTrue( df.exists() );
        assertTrue( df.isDirectory() );

        for ( int i = 0 ; i < 10 ; i++ )
        {
            var newfile =
                    new File( df, UUID.randomUUID().toString() );
            newfile.createNewFile();
            assertTrue(
                    newfile.exists() );
            assertTrue(
                    df.equals( newfile.getParentFile() ) );
        }

        assertEquals( 10, df.listFiles().length );

        FileUtil.delete( df );
        assertFalse( df.exists() );
    }

    @Test
    public void testDeleteDirWithOpenFile() throws Exception
    {
        if ( PlatformType.getPlatform() != PlatformType.WINDOWS )
            return;
        var dp = Files.createTempDirectory( getClass().getSimpleName() );
        var df = dp.toFile();

        assertTrue( df.exists() );
        assertTrue( df.isDirectory() );

        File file = new File( df, "testDeleteDirWithOpenFile" );

        // Open file.
        try ( var fos = new FileWriter( file ) )
        {
            fos.write( "testDeleteDirWithOpenFile" );
            fos.flush();
            // File is open and cannot be deleted.
            assertFalse( FileUtil.delete( df ) );
        }

        // File is now closed.
        assertTrue( FileUtil.delete( df ) );
        assertFalse( df.exists() );
    }
}
