/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-2024 Michael G. Binz
 */
package org.smack.util.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class ResourceUtilTest
{
    @Test
    public void testLoadResource() throws Exception
    {
        var resource =
                ResourceUtil.loadResource( getClass(), "micbinz_x.ascii" );
        String micbinz =
                new String( resource, StandardCharsets.US_ASCII );
        assertEquals(
                "micbinz",
                micbinz );
    }

    @Test
    public void testLoadResourceBadName() throws Exception
    {
        try
        {
            ResourceUtil.loadResource( getClass(), "micbinz.no" );
            fail( "Must not reach this." );
        }
        catch (Exception ignore) {
        }
    }

    @Test
    public void testLoadProperties() throws Exception
    {
        var p =
                ResourceUtil.loadProperties(
                        getClass(),
                        "ResourceMapTest.properties" );
        assertNotNull( p );
        assertTrue( p.size() > 0 );
        assertEquals( "314", p.getProperty( "314" ) );
        assertEquals( "314", p.get( "314" ) );
        assertEquals( "_313_", p.getProperty( "notDefined", "_313_" ) );
    }

    @Test
    public void testLoadPropertiesRaw() throws Exception
    {
        var resource =
                ResourceUtil.loadResource( getClass(), "ResourceMapTest.properties" );
        assertNotNull( resource );

        Properties result = new Properties();

        try ( var stream = new ByteArrayInputStream( resource ) )
        {
            result.load( stream );
            assertTrue( result.size() > 0 );
        }
    }
}
