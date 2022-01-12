package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Month;

import org.junit.Test;

public class EnumArrayTest
{
    @Test
    public void testEnumArray()
    {
        EnumArray<Month,Integer> array =
                new EnumArray<>( Month.class, Integer.MIN_VALUE );

        assertEquals( 12,array.size() );
        array.set( Month.SEPTEMBER, 9 );
        assertEquals( 9,(int)array.get( Month.SEPTEMBER ) );
        assertEquals( Integer.MIN_VALUE,(int)array.get( Month.JANUARY ) );
        array.clear();
        assertEquals( Integer.MIN_VALUE,(int)array.get( Month.SEPTEMBER ) );
        assertEquals( Integer.MIN_VALUE,(int)array.getNull() );
    }
    @Test
    public void testEnumArray2()
    {
        EnumArray<Month,Integer> array =
                new EnumArray<>( Month.class, null );

        array.set( Month.SEPTEMBER, 9 );
        assertEquals( 9,(int)array.get( Month.SEPTEMBER ) );
        assertNull( array.get( Month.JANUARY ) );
    }
}
