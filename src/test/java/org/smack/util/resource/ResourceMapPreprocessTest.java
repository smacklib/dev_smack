/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-2024 Michael G. Binz
 */
package org.smack.util.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;

import org.junit.jupiter.api.Test;

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
