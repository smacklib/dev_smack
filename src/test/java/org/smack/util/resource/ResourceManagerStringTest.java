package org.smack.util.resource;

import static org.junit.Assert.assertEquals;

import org.jdesktop.util.ResourceManager.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smack.util.ServiceManager;

/**
 *
 *
 * @version $Revision$
 * @author Michael Binz
 */
public class ResourceManagerStringTest
{
    private final ResourceManager _rm =
            ServiceManager.getApplicationService( ResourceManager.class );

    @Resource
    private String stringResource;
    @Resource
    private String[] stringArrayResource;

    @Before
    public void testInit()
    {
        _rm.injectResources( this );
    }

    @Test
    public void testString()
    {
        // See https://stackoverflow.com/questions/2406975/how-to-escape-the-equals-sign-in-properties-files
        // Also see my comments in StringStriongConverter.
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

        Assert.assertArrayEquals( values, stringArrayResource );
    }
}
