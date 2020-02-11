/* $Id$
 *
 * Common.
 *
 * Copyright © 2005-2012 Michael G. Binz
 */
package org.smack.util;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Objects;



/**
 * Math utilities.
 *
 * @version $Revision$
 * @author Michael Binz
 * @author Dave Hale, Landmark Graphics, 01/24/96
 */
public class MathUtil
{
    /**
     * @return True if the passed integer is odd.
     * @param integer The value to test.
     */
    public static boolean isOdd( long integer )
    {
        return (integer & 1) == 1;
    }

    /**
     * @return True if the passed integer is even.
     * @param integer The value to test.
     */
    public static boolean isEven( long integer )
    {
        return ! isOdd( integer );
    }

    /**
     * Computes the value of the polynomial with the factors from c for x.
     * The implementation uses the Horner-algorithm.
     *
     * @param x The value x used for the polynomial.
     * @param c The polynomial factors from n to 0. This is required, the
     * length of the array has to be at least one.
     * @return p(x).
     */
    public static double computePolynomial( double x, double[] c )
    {
        Objects.requireNonNull( c );
        if ( c.length < 0 )
            throw new IllegalArgumentException( "Length of c less than one." );

        double result = c[0];

        for ( int i = 1 ; i < c.length ; i++ )
            result = x * result + c[i];

        return result;
    }

    /**
     * Round a double to an integer.
     *
     * @param d The double to round.
     * @return The rounded result.
     */
    public static int round( double d )
    {
        return (int)Math.round( d );
    }

    /**
     * Compute the integer distance between points a and b.
     *
     * @param ax The x value of point a.
     * @param ay The y value of point a.
     * @param bx The x value of point b.
     * @param by The y value of point b.
     * @return The distance between points a and b.
     */
    public static int distanceInt( int ax, int ay, int bx, int by )
    {
        int a = ax - bx;
        int b = ay - by;

        if ( a == 0 )
            return Math.abs( b );
        if ( b == 0 )
            return Math.abs( a );

        return round( Math.hypot( a, b ) );
    }

    /**
     * Compute the integer distance between two points.
     *
     * @param a The first point.
     * @param b The second point.
     * @return The distance between points a and b.
     */
    public static int distanceInt( Point a, Point b )
    {
        return distanceInt( a.x, a.y, b.x, b.y );
    }

    /**
     * Compute the distance between points a and b.
     *
     * @param ax The x coordinate of point a.
     * @param ay The y coordinate of point a.
     * @param bx The x coordinate of point b.
     * @param by The y coordinate of point b.
     * @return The distance between points a and b.
     */
    public static double distance( double ax, double ay, double bx, double by )
    {
        double a = ax - bx;
        double b = ay - by;

        if ( a == 0 )
            return Math.abs( b );
        if ( b == 0 )
            return Math.abs( a );

        return Math.hypot( a, b );
    }

    /**
     * Pythagoras is a^2 + b^2 = c^2.  This operation computes b from a and c,
     * i.e. returns sqrt( c^2 - a^2 ).  Note that this same can be used to
     * compute a.
     *
     * @param a Distance a.
     * @param hypotenuse Distance c.
     * @return Distance b.
     */
    public static int pythagoras( int hypotenuse, int a )
    {
        if ( hypotenuse < a )
            throw new IllegalArgumentException( hypotenuse + " < " + a );

        double ad = a * a;
        double cd = hypotenuse * hypotenuse;

        return round( Math.sqrt( cd - ad ) );
    }

    /**
     * Pythagoras is a^2 + b^2 = c^2.  This operation computes b from a and c,
     * i.e. returns sqrt( c^2 - a^2 ).  Note that this same can be used to
     * compute a.
     *
     * @param a Distance a.
     * @param hypotenuse Distance c.
     * @return Distance b.
     */
    public static double pythagorasD( double hypotenuse, double a )
    {
        if ( hypotenuse < a )
            throw new IllegalArgumentException( hypotenuse + " < " + a );

        double ad = a * a;
        double cd = hypotenuse * hypotenuse;

        return Math.sqrt( cd - ad );
    }

    /**
     * Find the maximum of the passed numbers.
     *
     * @param first The first number.
     * @param second The second number.
     * @param more More numbers.
     * @return The largest number from the parameter list.
     */
    public static int max( int first, int second, int... more )
    {
        int result = Math.max( first, second );

        if ( more == null || more.length == 0 )
            return result;

        for ( int c : more )
            result = Math.max( result, c );

        return result;
    }

    /**
     * Find the minimum of the passed numbers.
     *
     * @param first The first number.
     * @param second The second number.
     * @param more More numbers.
     * @return The smallest number from the parameter list.
     */
    public static int min( int first, int second, int... more )
    {
        int result = Math.min( first, second );

        if ( more == null || more.length == 0 )
            return result;

        for ( int c : more )
            result = Math.min( result, c );

        return result;
    }

    /**
     * Computes a point that has a given distance from P(0,0) and is on a line
     * with a given angle from P.
     *
     * @param angle The angle of the ray.
     * @param distance The distance of the target point.
     * @return The target point.
     */
    public static Point2D pointWithDistanceFromA( float angle, float distance )
    {
        // Ensure that we stay in the 360 degree range.
        angle %= 360.0f;

        return pointWithDistanceFromA(
                sector( angle ),
                Math.tan( Math.toRadians( angle )),
                distance );
    }

    private static int sector( float angle )
    {
        if ( angle > 3*90 )
            return 3;
        if ( angle > 2*90 )
            return 2;
        if ( angle > 1*90 )
            return 1;

        return 0;
    }

    private static Point2D pointWithDistanceFromA( int sector, double m, float distance )
    {
        double resultX;
        double resultY;

        // Check this!
        if ( m != 0.0 )
        {
            double intersection = intersectLineAndCircle( m, distance );
            resultY = intersection;
            resultX = Math.abs( intersection / m );
        }
        else
        {
            resultX = distance;
            resultY = 0;
        }

        switch ( sector )
        {
            case 0:
                break;
            case 1:
                resultX = -resultX;
                break;
            case 2:
                resultX = -resultX;
                resultY = -resultY;
                break;
            case 3:
                resultY = -resultY;
                break;
        }

        return new Point2D.Double( resultX, resultY );
    }

    /**
     *
     * @param lineSlope
     * @param circleRadius
     * @return
     */
    private static double intersectLineAndCircle( double lineSlope, double circleRadius )
    {
        double radiusSquare =
                circleRadius * circleRadius;
        double slopeSquare =
                lineSlope * lineSlope;

        double squared =
                radiusSquare / (1.0d + slopeSquare);

        return Math.abs( lineSlope * Math.sqrt( squared ) );
    }

    /**
     * Create a random number in the passed range.
     *
     * @param lo The lower border.
     * @param hi The upper border.
     * @return A random number in the range [lo .. hi].
     */
    public static int randomBetween( int lo, int hi )
    {
        if ( lo >= hi )
            throw new IllegalArgumentException( "lo >= hi" );

        return lo + round( (hi-lo) * Math.random() );
    }

    /**
     * Ensure that the passed value is within the range of min to max.
     *
     * @param min The lower limit.
     * @param max The upper limit.
     * @param value The value to clamp.
     * @return The clamped value.
     */
    public static double clamp( double min, double max, double value )
    {
        if (value < min)
            return min;
        else if (value > max)
            return max;

        return value;
    }

    /**
     * Hidden constructor.
     */
    private MathUtil()
    {
        throw new AssertionError();
    }
}
