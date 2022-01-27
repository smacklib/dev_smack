package org.smack.util.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Test;

public class ResourceMapPreprocessTest
{
    @Test
    public void testPreprocessing() throws Exception
    {
        var map = ResourceMap.getResourceMap( getClass() );
        assertNotNull( map );

        assertNull( map.get( "null" ) );

        assertEquals( "1", map.get( "eins" ) );
        assertEquals( "2", map.get( "zwei" ) );
        assertEquals( "3", map.get( "drei" ) );

        assertEquals( "1", map.get( "one" ) );
        assertEquals( "11", map.get( "oneone" ) );
        assertEquals( "123", map.get( "onetwothree" ) );
        assertEquals( "123123123", map.get( "triple.onetwothree" ) );
        assertEquals( "123-123-123", map.get( "triple-dash.onetwothree" ) );

        assertEquals( "${", map.get( "escaped" ) );

        assertEquals( "prefix3", map.get( "withprefix" ) );

        assertTrue( map.containsKey( "atURL_1" ) );
        assertTrue( map.containsKey( "atURL_2" ) );
        new URL( map.get( "atURL_1" ) );
        assertEquals(
                map.get( "atURL_1" ),
                map.get( "atURL_2" ) );
    }
}
