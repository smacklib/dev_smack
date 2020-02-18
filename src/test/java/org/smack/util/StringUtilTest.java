/*
 * Copyright © 2019 Michael Binz.
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test string utils
 */
public class StringUtilTest
{
    @Test
    public void testEmptyString() {
        assertNotNull( StringUtil.EMPTY_STRING.length() );
        assertEquals( 0, StringUtil.EMPTY_STRING.length() );
    }

    @Test
    public void testTrimQuotes() {
        String toTrim = "\"B2E_MUX_+LC32_FMS_CAN_dbc_2016_31a.xml\"";
        assertEquals("B2E_MUX_+LC32_FMS_CAN_dbc_2016_31a.xml", StringUtil.trim(toTrim, "\""));
    }

    @Test
    public void testTrim() {
        String toTrim = "313";
        String trimmed =
                StringUtil.trim( toTrim, "x" );
        assertEquals(
                "313",
                trimmed );
        // If no characters were trimmed, we expect identity.
        assertTrue(
                toTrim == trimmed );
    }

    @Test
    public void testTrim0() {
        String toTrim = "$Test$";
        String trimmed =
                StringUtil.trim( toTrim, "$" );
        assertEquals( "Test", trimmed );
    }

    @Test
    public void testTrim2() {
        String toTrim = "$$$$Te$st$$$$";
        String trimmed =
                StringUtil.trim( toTrim, "$" );
        assertEquals( "Te$st", trimmed );
    }

    @Test
    public void testTrim3() {
        String toTrim = "abcTestdef";
        String trimmed =
                StringUtil.trim( toTrim, "abcdef" );
        assertEquals( "Test", trimmed );
    }
    @Test
    public void testTrim4() {
        String toTrim = "abcdef";
        String trimmed =
                StringUtil.trim( toTrim, "abcdef" );
        assertEquals( "", trimmed );
    }
    @Test
    public void testTrim5() {
        String toTrim = "abcTest";
        String trimmed =
                StringUtil.trim( toTrim, "abcdef" );
        assertEquals( "Test", trimmed );
    }
    @Test
    public void testTrim6() {
        String toTrim = "Testabc";
        String trimmed =
                StringUtil.trim( toTrim, "abcdef" );
        assertEquals( "Test", trimmed );
    }

    @Test
    public void testSplitQuote()
    {
        // Test split quote.
        String[] testCases = {
                // Plain
                "ab cd ef",
                // Whitespace is tab.
                "ab\tcd\tef",
                // Whitespace is mixed and at the end.
                "ab\tcd ef\t \t \t \t \tgh \t",
                // Quoted simple.
                "ab \"cd ef\" gh",
                // Quoted leading and trailing spaces.
                "ab \" cd ef \" gh",
                // Last quote not terminated, trailing space.
                "ab \" cd ef ",
                // Empty string.
                "ab \"\" cd",
                // Pathological: ab" cd ef" -> "ab cd ef"
                "ab\" cd ef ",
                // Empty string at eol.
                "michael \""
        };

        for ( String c : testCases )
        {
            System.err.println( "parseQuoted( '" + c + "' )" );
            for ( String c1 : StringUtil.splitQuoted( c ) )
                System.err.println( "'" + c1 + "'" );
        }
    }
}
