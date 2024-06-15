/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2020-21 Michael G. Binz
 */
package org.smack.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smack.util.ServiceManager;

public class ApplicationPropertiesTest
{
    @BeforeEach
    public void setup()
    {
        System.out.println( getClass().getName() + " : setup" );
    }
    @AfterEach
    public void cleanup()
    {
        System.out.println( getClass().getName() + " : cleanup" );
    }

    @Test
    public void plain() throws IOException
    {
//        ServiceManager.clear();
//        ApplicationContext.init( getClass() );
//        var aps = ServiceManager.getApplicationService(
//                ApplicationProperties.class );
    }

    @Test
    public void plainFail() throws IOException
    {
        assertThrows(
                RuntimeException.class,
                () -> {
                    ServiceManager.clear();
                    ServiceManager.getApplicationService(
                            ApplicationProperties.class );
                } );
    }
}

