/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022 Michael G. Binz
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DoubleHolderTest
{
    @Test()
    public void basic()
    {
        double value = 313.0D;
        double valuePlus1 = value + 1.0D;

        DoubleHolder i =
                new DoubleHolder( value );
        assertEquals( value, i.get(), 0 );

        i.set( i.get() + 1D );
        assertEquals( valuePlus1, i.get(), 0 );
        assertEquals( "314.0", i.toString() );
    }

    @Test()
    public void empty()
    {
        DoubleHolder i =
                new DoubleHolder();
        assertEquals(
                0,
                i.get(),
                0 );
        assertEquals(
                "0.0", i.toString() );
    }
}
