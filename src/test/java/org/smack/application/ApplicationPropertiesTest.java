/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2020-21 Michael G. Binz
 */
package org.smack.application;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smack.util.ServiceManager;

public class ApplicationPropertiesTest
{
    @Before
    public void setup()
    {

    }
    @After
    public void cleanup()
    {

    }

    @Test
    public void plain() throws IOException
    {
        ServiceManager.clear();
        ApplicationContext.init( getClass() );
        var aps = ServiceManager.getApplicationService(
                ApplicationProperties.class );
    }

    @Test( expected = java.lang.RuntimeException.class )
    public void plainFail() throws IOException
    {
        ServiceManager.clear();
        ServiceManager.getApplicationService(
                ApplicationProperties.class );
    }
}
