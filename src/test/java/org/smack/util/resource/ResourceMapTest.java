/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-2024 Michael G. Binz
 */
package org.smack.util.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ResourceMapTest
{
    @Test
    public void testQualifiedLookup() throws Exception
    {
        var map =
                ResourceMap.getResourceMap( getClass() );
        assertNotNull( map );
        assertTrue( map.size() > 0 );
        {
            var val = map.get( "ResourceMapTest.qualified" );
            assertNotNull( val );
            assertEquals( "qualified", val );
        }
        {
            var val = map.get( "qualified" );
            assertNotNull( val );
            assertEquals( "qualified", val );
        }
    }

    @Test
    public void testUnqualifiedLookup() throws Exception
    {
        var map =
                ResourceMap.getResourceMap( getClass() );
        assertNotNull( map );
        assertTrue( map.size() > 0 );
        {
            var val = map.get( "ResourceMapTest.unqualified" );
            assertNotNull( val );
            assertEquals( "unqualified", val );
        }
        {
            var val = map.get( "unqualified" );
            assertNotNull( val );
            assertEquals( "unqualified", val );
        }
    }

    @Test
    public void testGetAs() throws Exception
    {
        var map =
                ResourceMap.getResourceMap( getClass() );
        assertNotNull( map );
        assertTrue( map.size() > 0 );
        {
            var val = map.getAs( "ResourceMapTest.314", Short.class );
            assertNotNull( val );
            assertEquals( 314, val.shortValue() );
        }
        {
            int val = map.getAs( "313", Integer.class );
            assertNotNull( val );
            assertEquals( 313, val );
        }
    }

    @Test
    public void testGetAsDefault() throws Exception
    {
        var map =
                ResourceMap.getResourceMap( getClass() );
        assertNotNull( map );
        assertTrue( map.size() > 0 );
        {
            var val = map.getAs( "unqualified", Short.class, (short)313 );
            assertEquals( 313, val.shortValue() );
        }
        {
            var val = map.getAs( "314", Short.class, (short)0 );
            assertEquals( 314, val.shortValue() );
        }
    }

    @Test
    public void testResourcesExt() throws Exception
    {
        var map =
                ResourceMap.getResourceMapExt( getClass() );
        assertNotNull( map );
        assertTrue( map.size() > 0 );
        {
            var val = map.get( "ResourceMapTest.unqualified" );
            assertNotNull( val );
            assertEquals( "unqualified", val );
        }
        {
            var val = map.get( "unqualified" );
            assertNotNull( val );
            assertEquals( "unqualified", val );
        }
    }

    @Test
    public void testNoResourcesExt() throws Exception
    {
        var map =
                ResourceMap.getResourceMapExt( String.class );
        assertNotNull( map );
        assertTrue( map.size() == 0 );
    }

    @Test
    public void testGetFormatted() throws Exception
    {
        var map =
                ResourceMap.getResourceMap( getClass() );
        assertNotNull( map );

        var val = map.getFormatted( "format_ss", "Don", "ald" );
            assertEquals( "Donald", val );
    }
}
