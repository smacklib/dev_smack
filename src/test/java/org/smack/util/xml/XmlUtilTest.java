package org.smack.util.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Test;
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
            var styleSheet =  new CheckableInputStream(
                    getClass().getResourceAsStream( "cdd_did.xsl" ) );
            var xml = new CheckableInputStream(
                    getClass().getResourceAsStream( "example.cdd.xml" ) );

            String result = XmlUtil.transform(
                    styleSheet,
                    xml );

            assertTrue( styleSheet.isClosed() );
            assertTrue( xml.isClosed() );
            assertNotNull( result );
            assertEquals( 15824, result.split( "\\R" ).length );
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
        try ( var styleSheet = getClass().getResourceAsStream( "ic_car.xml" ) )
        {
            String result =
                    XmlUtil.getXPath(
                            styleSheet,
                            "/vector/@micbinz" );
            assertNotNull( result );
            assertNotNull( "binz" );
        }
        try ( var styleSheet = getClass().getResourceAsStream( "ic_car.xml" ) )
        {
            String result =
                    XmlUtil.getXPath( styleSheet, "/vector/@android:height" );
            assertNotNull( result );
            assertNotNull( "24dp" );
        }
        try ( var styleSheet = getClass().getResourceAsStream( "ic_car.xml" ) )
        {
            String result =
                    XmlUtil.getXPath( styleSheet, "/vector/path/@android:fillColor" );
            assertNotNull( result );
            assertNotNull( "#FF000000" );
        }
        try ( var styleSheet = getClass().getResourceAsStream( "ic_car.xml" ) )
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
        CheckableInputStream cis = new CheckableInputStream(
                getClass().getResourceAsStream( "ic_car.xml" ) );

        String result =
                XmlUtil.getXPath(
                        cis,
                        "/gibs/nich" );
        assertNotNull( result );
        assertEquals( StringUtil.EMPTY_STRING, result );
        assertTrue( cis.isClosed() );
    }

    @Test
    public void testXpathElement() throws Exception
    {
        String result =
                XmlUtil.getXPath(
                        getClass().getResourceAsStream( "ic_car.xml" ),
                        "/vector/donaldian" );
        assertNotNull( result );
        assertEquals( "313", result );
    }

    @Test
    public void testXpathConversion() throws Exception
    {
        try ( var styleSheet = getClass().getResourceAsStream( "ic_car.xml" ) )
        {
            int result =
                    XmlUtil.getXPathAs(
                            Integer::parseInt,
                            styleSheet,
                            "/vector/donaldian"  );
            assertEquals( 313, result );
        }
        try ( var styleSheet = getClass().getResourceAsStream( "ic_car.xml" ) )
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
        try ( var styleSheet = getClass().getResourceAsStream( "ic_car.xml" ) )
        {
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

    @Test
    public void testXpathNodes() throws Exception
    {
        CheckableInputStream cis = new CheckableInputStream(
                getClass().getResourceAsStream( "nodeTest.xml" ) );

        var list =
                XmlUtil.getXPathNodes(
                        cis,
                        "/vector/donaldians/donaldian" );
        assertTrue( cis.isClosed() );
        assertNotNull( list );
        assertEquals( 4, list.size() );
        assertEquals( "313", list.get( 0 ) );
        assertEquals( "314", list.get( 1 ) );
        assertEquals( "315", list.get( 2 ) );
        assertEquals( "316", list.get( 3 ) );
    }

    @Test
    public void testXpathNodes2() throws Exception
    {
        CheckableInputStream cis = new CheckableInputStream(
                getClass().getResourceAsStream( "nodeTest.xml" ) );

        var result =
                XmlUtil.getXPathNodes(
                        cis,
                        new String[] {"/*/donaldian"} );
        assertTrue( cis.isClosed() );
        assertNotNull( result );
        assertEquals( 1, result.size() );
        var list = result.get( 0 );
        assertNotNull( list );
        assertEquals( 1, list.size() );
        assertEquals( "262", list.get( 0 ) );

    }
    @Test
    public void testXpathNodes3() throws Exception
    {
        CheckableInputStream cis = new CheckableInputStream(
                getClass().getResourceAsStream( "nodeTest.xml" ) );

        var list =
                XmlUtil.getXPathNodes(
                        cis,
                        "//donaldian" );
        assertNotNull( list );
        assertEquals( 5, list.size() );
        assertEquals( "313", list.get( 0 ) );
        assertEquals( "314", list.get( 1 ) );
        assertEquals( "315", list.get( 2 ) );
        assertEquals( "316", list.get( 3 ) );
        assertEquals( "262", list.get( 4 ) );
    }
}
