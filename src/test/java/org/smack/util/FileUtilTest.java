/*
 * Copyright © 2020 Michael Binz.
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.StringReader;

import org.junit.Test;

/**
 * Test file utilities.
 */
public class FileUtilTest
{
    @Test
    public void testReadLines() throws Exception
    {
        var toTrim =
                "1\n"
                        + "2\n"
                        + "3\n"
                        + "4\n";
        var sr =
                new StringReader( toTrim );
        var list =
                FileUtil.readLines( sr );
        assertEquals( 4, list.size() );

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
        var toTrim =
                "1\n"
                        + "2\n"
                        + "3\n"
                        + "4\n\n";
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
                        "4"
                };
        var toTrim =
                StringUtil.concatenate(
                        StringUtil.EOL,
                        lines );
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
                lines.length,
                list.size() );
        assertTrue( isClosed[0] );
    }
}
