/*
 * Copyright © 2019 Daimler TSS.
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test string utils
 */
public class StringUtilTest {

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
}