/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022 Michael G. Binz
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LongHolderTest
{
    @Test()
    public void basic()
    {
        LongHolder i =
                new LongHolder( 313L );
        assertEquals( 313L, i.get() );

        i.set( i.get() + 1 );
        assertEquals( 314L, i.get() );
        assertEquals( "314", i.toString() );
    }

    @Test()
    public void empty()
    {
        LongHolder i =
                new LongHolder();
        assertEquals(
                0,
                i.get() );
        assertEquals(
                "0", i.toString() );
    }
}
