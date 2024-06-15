/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-2024 Michael G. Binz
 */
package org.smack.util.resource;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager.Resource;

public class ResourceManagerStringTest
{
    private final ResourceManager _rm =
            ServiceManager.getApplicationService( ResourceManager.class );

    @Resource
    private String stringResource;
    @Resource
    private String[] stringArrayResource;

    @BeforeEach
    public void testInit()
    {
        _rm.injectResources( this );
    }

    @Test
    public void testString()
    {
        // See https://stackoverflow.com/questions/2406975/how-to-escape-the-equals-sign-in-properties-files
        // Also see comments in StringStringConverter.
        assertEquals( "\"Michael Binz\"", stringResource );
    }

    @Test
    public void testStringArray()
    {
        String[] values = {
                "Januar",
                "Feb uar",
                "März",
                "April",
                "Mai",
                "Juni",
                "Juli",
                "August",
                "September",
                "Oktober",
                "November",
                "Dezember"
        };

        assertArrayEquals( values, stringArrayResource );
    }
}
