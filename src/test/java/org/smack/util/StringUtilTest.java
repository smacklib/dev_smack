/*
 * Copyright © 2019 Michael Binz.
 */
package org.smack.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.LineNumberReader;
import java.io.StringReader;

import org.junit.Test;

/**
 * Test string utils
 */
public class StringUtilTest
{
    @Test
    public void testToHex()
    {
        byte[] ba =
                new byte[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
        String s =
                StringUtil.toHex( ba );
        assertEquals( "000102030405060708090a0b0c0d0e0f", s );

        byte[] returned =
                StringUtil.fromHex( s );
        assertArrayEquals(
                ba,
                returned );
    }

    @Test
    public void testToHex2()
    {
        // One too short.
        String s = "000102030405060708090a0b0c0d0e0";

        byte[] ba =
                StringUtil.fromHex( s );
        assertNull( ba );
    }

    @Test
    public void testToHex3()
    {
        String s = "Donald Duck";

        byte[] ba =
                StringUtil.fromHex( s );
        assertNull( ba );
    }

    @Test
    public void testEmptyString() {
        assertNotNull( StringUtil.EMPTY_STRING );
        assertEquals( 0, StringUtil.EMPTY_STRING.length() );
        assertTrue( StringUtil.EMPTY_STRING.isEmpty() );
    }

    @Test
    public void testEol() throws Exception {
        assertNotNull( StringUtil.EOL );
        String twoLines = "1" + StringUtil.EOL + "2" + StringUtil.EOL;

        var sr = new LineNumberReader(
                new StringReader( twoLines ) );

        assertEquals( "1", sr.readLine() );
        assertEquals( "2", sr.readLine() );
        assertNull( sr.readLine() );
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
    public void testSplitQuote0()
    {
        // Plain
        assertArrayEquals(
                new String[]{"ab","cd","ef"},
                StringUtil.splitQuoted(
                        "ab cd ef" ) );
    }

    @Test
    public void testSplitQuote1()
    {
        // Whitespace is tab.
        assertArrayEquals(
                new String[]{"ab","cd","ef"},
                StringUtil.splitQuoted(
                        "ab\tcd\tef" ) );
    }
    @Test
    public void testSplitQuote2()
    {
        // Whitespace is mixed and at the end.
        assertArrayEquals(
                new String[]{"ab","cd","ef","gh"},
                StringUtil.splitQuoted(
                        "ab\tcd ef\t \t \t \t \tgh \t" ) );
    }
    @Test
    public void testSplitQuote3()
    {

        // Quoted simple.
        assertArrayEquals(
                new String[]{"ab","cd ef","gh"},
                StringUtil.splitQuoted(
                        "ab \"cd ef\" gh" ) );
    }
    @Test
    public void testSplitQuote4()
    {
        // Quoted leading and trailing spaces.
        assertArrayEquals(
                new String[]{"ab"," cd ef ","gh"},
                StringUtil.splitQuoted(
                        "ab \" cd ef \" gh" ) );
    }
    @Test
    public void testSplitQuote5()
    {
        // Last quote not terminated, trailing space.
        assertArrayEquals(
                new String[]{"ab"," cd ef "},
                StringUtil.splitQuoted(
                        "ab \" cd ef " ) );
    }
    @Test
    public void testSplitQuote6()
    {
        // Empty string.
        assertArrayEquals(
                new String[]{"ab", StringUtil.EMPTY_STRING, "cd"},
                StringUtil.splitQuoted(
                        "ab \"\" cd" ));
    }
    @Test
    public void testSplitQuote7()
    {
        // Pathological: ab" cd ef" -> "ab cd ef"
        assertArrayEquals(
                new String[]{"ab cd ef "},
                StringUtil.splitQuoted(
                        "ab\" cd ef " ) );
    }
    @Test
    public void testSplitQuote8()
    {
        // Empty string at eol.
        assertArrayEquals(
                new String[]{"michael", StringUtil.EMPTY_STRING},
                StringUtil.splitQuoted(
                        "michael \"" ) );
    }
    @Test
    public void testSplitQuote9()
    {
        // Empty string.
        assertArrayEquals(
                new String[]{"ab", StringUtil.EMPTY_STRING, "cd"},
                StringUtil.splitQuoted(
                        '\'',
                        "ab '' cd" ));
    }

    @Test
    public void testQuote0()
    {
        String q = StringUtil.quote( '!', null );
        // Empty string.
        assertEquals(
                "!!", q );
        q = StringUtil.unquote( '!', q );
        // Empty string.
        assertEquals( StringUtil.EMPTY_STRING, q );
    }
    @Test
    public void testQuote1()
    {
        String q = StringUtil.quote( null );
        // Empty string.
        assertEquals(
                "\"\"", q );
        q = StringUtil.unquote( q );
        // Empty string.
        assertEquals( StringUtil.EMPTY_STRING, q );
    }
    @Test
    public void testQuote2()
    {
        String q = StringUtil.quote( "micbinz" );
        // Empty string.
        assertEquals(
                "\"micbinz\"", q );
        q = StringUtil.unquote( q );
        // Empty string.
        assertEquals( "micbinz", q );
    }
    @Test
    public void testQuote3()
    {
        String q = StringUtil.quote( 'ß', "micbinz" );
        // Empty string.
        assertEquals(
                "ßmicbinzß", q );
        q = StringUtil.unquote( 'ß', q );
        // Empty string.
        assertEquals( "micbinz", q );
    }
    @Test
    public void testQuote4()
    {
        String q = StringUtil.quote( 'i', "micbinz" );
        // Empty string.
        assertEquals(
                "im\\icb\\inzi", q );
        q = StringUtil.unquote( 'i', q );
        // Empty string.
        assertEquals( "micbinz", q );
    }
}
