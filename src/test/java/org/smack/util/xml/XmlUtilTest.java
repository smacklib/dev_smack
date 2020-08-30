package org.smack.util.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Test;
import org.smack.util.Disposer;
import org.smack.util.StringUtil;

public class XmlUtilTest
{
    private File makeFileFromStream( InputStream is )
            throws Exception
    {
        File result = File.createTempFile(
                getClass().getSimpleName(),
                null );
        result.deleteOnExit();

        try {
            Files.copy( is, result.toPath(), StandardCopyOption.REPLACE_EXISTING );
        }
        finally {
            is.close();
        }

        return result;
    }

    @Test
    public void testTransformStream() throws Exception
    {
        try ( var d = new Disposer() )
        {
            var styleSheet = d.register(
                    getClass().getResourceAsStream( "cdd_did.xsl" ) );
            var xml =  d.register(
                    getClass().getResourceAsStream( "example.cdd.xml" ) );

            String result = XmlUtil.transform(
                    styleSheet,
                    xml );

            assertNotNull( result );
            assertEquals( 15824, result.split( "\\R" ).length );
        }
    }

    @Test
    public void testTransformFile() throws Exception
    {
        File styleSheet = makeFileFromStream(
                getClass().getResourceAsStream( "cdd_did.xsl" ) );
        File xml = makeFileFromStream(
                getClass().getResourceAsStream( "example.cdd.xml" ) );

        String result = XmlUtil.transform( styleSheet, xml );

        assertNotNull( result );
        assertEquals( 15824, result.split( "\\R" ).length );
    }

    @Test
    public void testXpath() throws Exception
    {
        File styleSheet = makeFileFromStream(
                getClass().getResourceAsStream( "ic_car.xml" ) );

        {
            String result =
                    XmlUtil.getXPath( styleSheet, "/vector/@micbinz" );
            assertNotNull( result );
            assertNotNull( "binz" );
        }
        {
            String result =
                    XmlUtil.getXPath( styleSheet, "/vector/@android:height" );
            assertNotNull( result );
            assertNotNull( "24dp" );
        }
        {
            String result =
                    XmlUtil.getXPath( styleSheet, "/vector/path/@android:fillColor" );
            assertNotNull( result );
            assertNotNull( "#FF000000" );
        }
        {
            var result =
                    XmlUtil.getXPath(
                            styleSheet,
                            "/vector/@micbinz",
                            "/vector/@android:height",
                            "/vector/path/@android:fillColor" );
            assertNotNull( result );
            assertEquals( 3, result.size() );
            assertEquals( "binz", result.get( 0 ) );
            assertEquals( "24dp", result.get( 1 ) );
            assertEquals( "#FF000000", result.get( 2 ) );
        }
    }

    @Test
    public void testXpathBad() throws Exception
    {
        File styleSheet = makeFileFromStream(
                getClass().getResourceAsStream( "ic_car.xml" ) );

        {
            String result =
                    XmlUtil.getXPath( styleSheet, "/gibs/nich" );
            assertNotNull( result );
            assertEquals( StringUtil.EMPTY_STRING, result );
        }
    }

    @Test
    public void testXpathElement() throws Exception
    {
        File styleSheet = makeFileFromStream(
                getClass().getResourceAsStream( "ic_car.xml" ) );

        {
            String result =
                    XmlUtil.getXPath( styleSheet, "/vector/donaldian" );
            assertNotNull( result );
            assertEquals( "313", result );
        }
    }
    @Test
    public void testXpathConversion() throws Exception
    {
        File styleSheet = makeFileFromStream(
                getClass().getResourceAsStream( "ic_car.xml" ) );

        {
            int result =
                    XmlUtil.getXPathAs(
                            Integer::parseInt,
                            styleSheet,
                            "/vector/donaldian"  );
            assertEquals( 313, result );
        }
        {
            var result =
                    XmlUtil.getXPathAs(
                            Integer::parseInt,
                            styleSheet,
                            "/vector/donaldian",
                            "/vector/donaldianInc" );
            assertNotNull( result );
            assertEquals( 2, result.size() );
            assertEquals( 313, (int)result.get( 0 ) );
            assertEquals( 314, (int)result.get( 1 ) );
        }
        try {
            XmlUtil.getXPathAs(
                    Integer::parseInt,
                    styleSheet,
                    "/gibs/nich" );
            fail();
        }
        catch ( NumberFormatException expected )
        {
        }
    }
}
