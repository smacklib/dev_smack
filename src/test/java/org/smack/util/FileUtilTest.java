/*
 * Copyright © 2020 Michael Binz.
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

/**
 * Test string utils
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
    }
}
