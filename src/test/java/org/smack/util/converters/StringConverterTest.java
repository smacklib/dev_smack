package org.smack.util.converters;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.time.Month;
import java.util.Locale;

import org.junit.Test;
import org.smack.util.ServiceManager;

public class StringConverterTest
{
    private final StringConverter _cvt =
            ServiceManager.getApplicationService( StringConverter.class );

    @Test
    public void testBoolean() throws Exception
    {
        assertEquals(
                Boolean.TRUE,
                _cvt.convert( Boolean.class, "true" ) );
        assertEquals(
                Boolean.FALSE,
                _cvt.convert( Boolean.class, "false" ) );
        assertEquals(
                Boolean.FALSE,
                _cvt.convert( Boolean.class, "FALSE" ) );
        assertEquals(
                Boolean.FALSE,
                _cvt.convert( Boolean.TYPE, "FALSE" ) );
        assertArrayEquals(
                new boolean[] { true, false, true },
                _cvt.convert( boolean[].class, "true False truE" ) );
    }
    @Test
    public void testShort() throws Exception
    {
        assertEquals(
                313,
                (short)_cvt.convert( Short.TYPE, "313" ) );
        assertEquals(
                0xf,
                (short)_cvt.convert( Short.TYPE, "0xf" ) );
        assertArrayEquals(
                new short[] { -1, 0, 1 },
                _cvt.convert( short[].class, "-1 0 1" ) );
    }

    @Test
    public void testInteger() throws Exception
    {
        assertEquals(
                313,
                (int)_cvt.convert( Integer.TYPE, "313" ) );
        assertEquals(
                0xf,
                (int)_cvt.convert( Integer.TYPE, "0xf" ) );
        assertArrayEquals(
                new int[] { -1, 0, 1 },
                _cvt.convert( int[].class, "-1 0 1" ) );
    }

    @Test
    public void testColor() throws Exception
    {
        // No alpha.
        assertEquals(
                Color.red,
                _cvt.convert( Color.class, "#ff0000" ) );
        // With alpha.
        assertEquals(
                Color.red,
                _cvt.convert( Color.class, "#ffff0000" ) );
        assertEquals(
                Color.red,
                _cvt.convert( Color.class, "red" ) );

        assertArrayEquals(
                new Color[] { Color.red, Color.green, Color.blue },
                _cvt.convert( Color[].class, "#ff0000 #ff00ff00 #0000ff" ) );
        assertArrayEquals(
                new Color[] { Color.red, Color.green, Color.blue },
                _cvt.convert( Color[].class, "red green blue" ) );

        try
        {
            assertEquals(
                    Color.red,
                    _cvt.convert( Color.class, "#ff00001" ) );
            fail();
        }
        catch ( IllegalArgumentException e )
        {

        }

        try
        {
            assertEquals(
                    Color.red,
                    _cvt.convert( Color.class, "gulp" ) );
            fail();
        }
        catch ( IllegalArgumentException e )
        {

        }
    }

    @Test
    public void testEnumSynthMonth() throws Exception
    {
        assertEquals(
                Month.SEPTEMBER,
                _cvt.convert( Month.class, "SEPTEMBER" ) );
        try
        {
            // Not case independent.
            _cvt.convert( Month.class, "September" );
            assertTrue( false );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals(
                    "Unknown enum value: 'September'.  Allowed values are JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER.",
                    e.getMessage() );
        }

        assertArrayEquals(
                new Month[] { Month.MAY, Month.APRIL, Month.DECEMBER },
                _cvt.convert( Month[].class, "MAY  APRIL  DECEMBER" ) );
    }

    @Test
    public void testStringSyntLocale() throws Exception
    {
        assertEquals(
                Locale.FRENCH,
                _cvt.convert( Locale.class, "fr" ) );
        assertArrayEquals(
                new Locale[] { Locale.ITALIAN, Locale.JAPANESE, Locale.ENGLISH },
                _cvt.convert( Locale[].class, "it ja  en" ) );
    }

    @Test
    public void testStringSyntUrl() throws Exception
    {
        var urlTxt = "https://www.google.de";
        assertEquals(
                new URL( urlTxt ),
                _cvt.convert( URL.class, urlTxt ) );
    }

    @Test
    public void testFont() throws Exception
    {
        var fontTxt = "Monospaced-PLAIN-12";
        assertEquals(
                new Font( Font.MONOSPACED, Font.PLAIN, 12 ),
                _cvt.convert( Font.class, fontTxt ) );
    }
    @Test
    public void testFont2() throws Exception
    {
        var fontTxt = "MOnospaced-PLAIN-12";
        try {
            _cvt.convert( Font.class, fontTxt );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "Unknown font name: MOnospaced", e.getMessage() );
        }
    }

    @Test
    public void testFont_non_existing() throws Exception
    {
        var fontTxt = "Donald-PLAIN-12";
        try {
            _cvt.convert( Font.class, fontTxt );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "Unknown font name: Donald", e.getMessage() );
        }
    }
}
