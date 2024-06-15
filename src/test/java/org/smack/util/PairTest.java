/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2023-2024 Michael G. Binz
 */
package org.smack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class PairTest
{
    @Test
    public void integerPair()
    {
        Pair<Integer, Integer> integerPair = new Pair<>( 1, 2 );

        assertEquals( 1, (int)integerPair.left );
        assertEquals( 2, (int)integerPair.right );
        assertEquals( "Pair{1, 2}", integerPair.toString() );
    }

    @Test
    public void nullPair()
    {
        Pair<Object, Object> integerPair = new Pair<>( null, null );

        assertNull( integerPair.left );
        assertNull( integerPair.right );
        assertEquals( "Pair{null, null}", integerPair.toString() );
    }

    @Test
    public void stringIntegerPair()
    {
        Pair<String, Integer> integerPair = new Pair<>( "micbinz", null );

        assertEquals( "micbinz", integerPair.left );
        assertNull( integerPair.right );
        assertEquals( "Pair{micbinz, null}", integerPair.toString() );
    }
}
