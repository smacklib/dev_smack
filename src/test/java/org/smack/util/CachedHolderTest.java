/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2023 Michael G. Binz
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CachedHolderTest
{
    private int _factoryCurrentValue = 313;

    private int _factoryCallCount = 0;

    private Integer intFactory()
    {
        _factoryCallCount++;

        return Integer.valueOf( _factoryCurrentValue++ );
    }

    @Test( expected = NullPointerException.class )
    public void ctorNull()
    {
        new CachedHolder<Integer>( null );
    }

    @Test()
    public void basic()
    {
        CachedHolder<Integer> c_int =
                new CachedHolder<Integer>( this::intFactory );
        {
            assertEquals( 0, c_int.getReadCount() );
            assertEquals( 0, _factoryCallCount );

            Integer value = c_int.get();

            assertEquals( 313, (int)value );
            assertEquals( 1, c_int.getReadCount() );
            assertTrue( value == c_int.get() );
            assertEquals( 2, c_int.getReadCount() );
        }
    }
}
