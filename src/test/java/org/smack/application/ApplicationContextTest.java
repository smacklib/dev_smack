/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2020-21 Michael G. Binz
 */
package org.smack.application;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class ApplicationContextTest
{
    @Test
    public void plain() throws IOException
    {
        var ac = new ApplicationContext( getClass() );

        assertEquals(
                getClass(),
                ac.getApplicationClass() );
        assertEquals(
                getClass().getSimpleName(),
                ac.getId() );
    }
}
