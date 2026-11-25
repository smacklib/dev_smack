/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022 Michael G. Binz
 */
package org.smack.util;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.smack.util.Continuation.Cont;
import org.smack.util.Continuation.Thunk;

public class ContinuationTest
{
    @SuppressWarnings("serial")
    static class TotalX extends Exception
    {
        public final int _total;

        public TotalX( int a, int b )
        {
            _total = a + b;
        }
    }

    private Thunk add( int a, int b, Cont<Integer> c )
    {
        return () -> c.accept( a + b );
    }

    private Thunk addd( int a, int b, Cont<Integer> c )
    {
        return () -> () -> c.accept( a + b );
    }

    private Thunk addx( int a, int b, Cont<Integer> c )
    {
        return () -> {
            throw new ArithmeticException( "addx" );
        };
    }

    @Test
    public void basic() throws Exception
    {
        Continuation<Integer, Exception> continuation =
                new Continuation<>( Exception.class );

        continuation.thunkCount(0);

        int result = continuation.toStack(
                cont -> add(3,4,cont) );

        assertEquals( 1, continuation.thunkCount() );
        assertEquals( 7, result );
    }

    @Test
    public void basic2() throws Exception
    {
        Continuation<Integer, Exception> continuation =
                new Continuation<>( Exception.class );

        continuation.thunkCount(0);

        int result = continuation.toStack(
                cont-> addd(3,4,cont) );

        assertEquals( 2, continuation.thunkCount() );
        assertEquals( 7, result );
    }

    @Test
    public void testSuccess() throws Exception
    {
        Continuation<Integer, Exception> continuation =
                new Continuation<>( Exception.class );

        int result = continuation.toStack(
                cont-> add(3,4,cont) );

        assertEquals( 7, result );
    }

    @Test
    public void testException()
    {
        Continuation<Integer, Exception> continuation =
                new Continuation<>( Exception.class );

        assertThrows(
                ArithmeticException.class,
                () -> continuation.toStack(
                    cont-> addx(3,4,cont) ) );
    }

    private Thunk addWithTotalX( int a, int b, Cont<Integer> c) throws TotalX
    {
        return () -> {
            throw new TotalX( 3, 4 );
            };
    }

    @Test
    public void testException2() throws Exception
    {
        Continuation<Integer, TotalX> continuation =
                new Continuation<>( TotalX.class );

        Holder<TotalX> aeh =
                new Holder<>();

        Cont<TotalX> handler =
                ae -> {
                    aeh.set( ae );
                    return null;
                };

        Integer result = continuation.toStack(
                cont -> addWithTotalX( 3, 4, cont ),
                handler );

        assertEquals( null, result );
        assertEquals( 7, aeh.get()._total );
    }

    private Thunk factorial( int n, Cont<BigDecimal> c )
    {
        return () -> {
            if ( n <= 1 )
                return c.accept( BigDecimal.ONE );
            return factorial(
                    n - 1,
                    res -> c.accept( BigDecimal.valueOf( n ).multiply( res ) ) );
        };
    }

    @Test
    public void testFactorial() throws Exception
    {
        Continuation<BigDecimal, Exception> continuation =
                new Continuation<>( Exception.class );

        BigDecimal result = continuation.toStack(
                cont -> factorial( 73, cont ) );

        assertEquals(
                new BigDecimal(
                        "4470115461512684340891257138125051110076800700282905015819080092370422104067183317016903680000000000000000"),
                result );
    }

    private Thunk tailRecursiveCountdown( int n, Cont<Integer> c )
    {
        return () -> {
            if ( n == 0 )
                return c.accept( n );
            return tailRecursiveCountdown( n - 1, c );
        };
    }

    @Test
    public void testTailRecursiveContinuation() throws Exception
    {
        int iterations = 1_000_000_000;
        Continuation<Integer, Exception> continuation =
                new Continuation<>( Exception.class );

        int result = continuation.toStack(
                cont -> tailRecursiveCountdown( iterations, cont ) );

        assertEquals( 0, result );
        assertEquals( iterations + 1L, continuation.thunkCount() );
    }
}
