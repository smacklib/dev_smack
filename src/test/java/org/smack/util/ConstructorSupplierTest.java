/*
 * Copyright © 2020 Michael Binz.
 */
package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;
import java.util.function.Supplier;

import org.junit.Test;

/**
 * Test file utilities.
 */
public class ConstructorSupplierTest
{
    @Test
    public void plain() throws Exception
    {
        Supplier<String> s =
                new ConstructorSupplier<String>( String.class );

        assertTrue( StringUtil.isEmpty( s.get() ) );
    }

    @Test
    public void error() throws Exception
    {
        try
        {
            new ConstructorSupplier<>( UUID.class );
            fail();
        }
        catch ( Exception e )
        {
            assertEquals( "No default constructor.",  e.getMessage() );
        }
    }
}
