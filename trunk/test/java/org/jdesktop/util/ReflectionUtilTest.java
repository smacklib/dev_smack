package org.jdesktop.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @version $Id$
 */
public class ReflectionUtilTest
{
    private static final int ZERO = 0;
    private static final int SEVEN = 7;

    @Test
    public void makeArrayTest()
    {
        {
            String[] seven =
                    ReflectionUtil.makeArray( String.class, SEVEN );
            assertEquals( SEVEN, seven.length );

            for ( int i = 0 ; i < seven.length ; i++ )
                assertNull( seven[i] );
        }
        {
            String[] zero =
                    ReflectionUtil.makeArray( String.class, ZERO );
            assertEquals( ZERO, zero.length );
        }

        try
        {
            String[] s = ReflectionUtil.makeArray( String.class, -1 );
            fail();
        }
        catch ( Exception expected )
        {
        }

        // Test not-allowed primitives.
        try
        {
            ReflectionUtil.makeArray( int.class, SEVEN );
            fail();
        }
        catch ( Exception expected )
        {

        }

    }
}
