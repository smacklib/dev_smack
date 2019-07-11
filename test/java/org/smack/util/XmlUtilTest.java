package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

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
    public void TestTransform8() throws Exception
    {
        File styleSheet = makeFileFromStream(
                getClass().getResourceAsStream( "cdd_did.xsl" ) );
        File xml = makeFileFromStream(
                getClass().getResourceAsStream( "example.cdd.xml" ) );

        String result = XmlUtil.transform( styleSheet, xml );

        assertNotNull( result );
        assertEquals( 540225, result.length() );
    }
}
