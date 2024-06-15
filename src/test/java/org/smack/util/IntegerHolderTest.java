/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022 Michael G. Binz
 */
package org.smack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IntegerHolderTest
{
    @Test
    public void basic()
    {
        IntegerHolder i =
                new IntegerHolder( 313 );
        assertEquals( 313, i.get() );

        i.set( i.get() + 1 );
        assertEquals( 314, i.get() );
        assertEquals( "314", i.toString() );
    }

    @Test()
    public void empty()
    {
        IntegerHolder i =
                new IntegerHolder();
        assertEquals(
                0,
                i.get() );
        assertEquals(
                "0", i.toString() );
    }
}
