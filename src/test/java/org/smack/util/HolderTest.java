/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022 Michael G. Binz
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class HolderTest
{
    @Test()
    public void basic()
    {
        Holder<Integer> i =
                new Holder<Integer>( 313 );
        assertEquals( 313, (int)i.get() );

        i.set( i.get() + 1 );
        assertEquals( 314, (int)i.get() );
        assertEquals( "314", i.toString() );
    }

    @Test()
    public void empty()
    {
        Holder<Integer> i =
                new Holder<Integer>( null );
        assertNull(
                i.get() );
        assertEquals(
                "null", i.toString() );
    }

    @Test()
    public void defaultEmpty()
    {
        Holder<Integer> i =
                new Holder<Integer>();
        assertNull(
                i.get() );
        assertEquals(
                "null", i.toString() );
    }
}
