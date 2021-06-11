package org.smack.util.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ResourceMapPreprocessInvalidKeyErrorTest
{
    @Test
    public void testInvalidKey() throws Exception
    {
        try
        {
            ResourceMap.getResourceMap( getClass() );
            fail();
        }
        catch ( Exception e )
        {
            var msg = e.getMessage();
            assertNotNull( msg );
            var split = msg.split( "@" );
            assertEquals( 3, split.length );
            split[0] = split[0].trim();
            split[1] = split[1].trim();
            split[2] = split[2].trim();
            assertEquals( "Invalid key 'ResourceMapPreprocessInvalidKeyErrorTest.'", split[0] );
            assertEquals( getClass().getSimpleName(), split[1] );

            var urlSuffix = getClass().getPackageName().replace( '.', '/' ) + "/";
            assertTrue( split[2].endsWith( urlSuffix ) );
        }
    }
}
