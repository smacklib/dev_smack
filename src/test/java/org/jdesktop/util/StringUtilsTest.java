/*
 * Copyright © 2019 Daimler TSS.
 */
package com.daimler.tcu.vit.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test string utils
 *
 * @author KAISEDE
 */
public class StringUtilsTest {

    @Test
    public void hexStringToByteArray() {
        byte[] result = new byte[] {00,02};
        assertArrayEquals("Wrong convert", result, StringUtils.hexStringToByteArray("0002"));
    }

    @Test
    public void hexStringToByteArrayStartWithWhitespace() {
        byte[] result = new byte[] {00,02};
        assertArrayEquals("Wrong convert", result, StringUtils.hexStringToByteArray(" 0002"));
    }

    @Test
    public void hexStringToByteArrayEndWithWhitespaces() {
        byte[] result = new byte[] {00,02};
        assertArrayEquals("Wrong convert", result, StringUtils.hexStringToByteArray("0002  "));
    }

    @Test
    public void hexStringToByteArrayNull() {
        assertNull("Not null", StringUtils.hexStringToByteArray("null"));
    }

    @Test
    public void testTrimQuotes() {
        String toTrim = "\"B2E_MUX_+LC32_FMS_CAN_dbc_2016_31a.xml\"";
        assertEquals("B2E_MUX_+LC32_FMS_CAN_dbc_2016_31a.xml", StringUtils.trim(toTrim, "\""));
    }

    @Test
    public void testTrim() {
        String toTrim = "313";
        String trimmed =
                StringUtils.trim( toTrim, "x" );
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
                StringUtils.trim( toTrim, "$" );
        assertEquals( "Test", trimmed );
    }

    @Test
    public void testTrim2() {
        String toTrim = "$$$$Te$st$$$$";
        String trimmed =
                StringUtils.trim( toTrim, "$" );
        assertEquals( "Te$st", trimmed );
    }

    @Test
    public void testTrim3() {
        String toTrim = "abcTestdef";
        String trimmed =
                StringUtils.trim( toTrim, "abcdef" );
        assertEquals( "Test", trimmed );
    }
    @Test
    public void testTrim4() {
        String toTrim = "abcdef";
        String trimmed =
                StringUtils.trim( toTrim, "abcdef" );
        assertEquals( "", trimmed );
    }
    @Test
    public void testTrim5() {
        String toTrim = "abcTest";
        String trimmed =
                StringUtils.trim( toTrim, "abcdef" );
        assertEquals( "Test", trimmed );
    }
    @Test
    public void testTrim6() {
        String toTrim = "Testabc";
        String trimmed =
                StringUtils.trim( toTrim, "abcdef" );
        assertEquals( "Test", trimmed );
    }
}
