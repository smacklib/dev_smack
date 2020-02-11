package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class MathUtilTest
{
    @Test
    public void testComputePolynomial()
    {
        double p = MathUtil.computePolynomial(
                1,
                new double[]{2.0d, 3.0d, 4.0d} );
        assertEquals( 9, p, 0 );
    }

    @Test
    public void testComputePolynomialBad1()
    {
        try
        {
            MathUtil.computePolynomial(
                    1,
                    new double[0] );
            fail( "No good." );
        }
        catch ( Exception ignore )
        {

        }
    }

    @Test
    public void testComputePolynomialBad2()
    {
        try
        {
            MathUtil.computePolynomial(
                    1,
                    null );
            fail( "No good." );
        }
        catch ( Exception ignore )
        {

        }
    }

    @Test
    public void testDistance()
    {
        for ( double i = -10 ; i < 10 ; i+=.5)
        {
            double d = MathUtil.distance( 0, i, 1, i );
            assertEquals( 1, d, 0 );
        }
        for ( double i = -10 ; i < 10 ; i+=.5 )
        {
            double d = MathUtil.distance( i, 0, i, 1 );
            assertEquals( 1, d, 0 );
        }
    }

    @Test
    public void testDistance14()
    {
            double d = MathUtil.distance( 0.0, 0.0, 1.0, 1.0 );
            assertEquals( Math.sqrt( 2.0 ), d, 0 );
    }

    @Test
    public void testMaximum()
    {
        int result = MathUtil.max( -1, 0 );
        assertEquals( 0, result );
        result = MathUtil.max( 1, -1, 0 );
        assertEquals( 1, result );
    }

    @Test
    public void testMinimum()
    {
        int result = MathUtil.min( -1, 0 );
        assertEquals( -1, result );
        result = MathUtil.min( 1, -1, 0 );
        assertEquals( -1, result );
    }

    @Test
    public void testEven()
    {
        assertTrue( MathUtil.isEven( -2 ) );
        assertTrue( MathUtil.isEven( 0 ) );
        assertTrue( MathUtil.isEven( 2 ) );

        assertFalse( MathUtil.isEven( -1 ) );
        assertFalse( MathUtil.isEven( 1 ) );
        assertFalse( MathUtil.isEven( 3 ) );
    }

    @Test
    public void testOdd()
    {
        assertTrue( MathUtil.isOdd( -1 ) );
        assertTrue( MathUtil.isOdd( 1 ) );

        assertFalse( MathUtil.isOdd( -2 ) );
        assertFalse( MathUtil.isOdd( 0 ) );
        assertFalse( MathUtil.isOdd( 2 ) );
    }
}
