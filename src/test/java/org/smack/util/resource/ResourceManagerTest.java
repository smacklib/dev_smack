package org.smack.util.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager.Resource;

/**
 *
 *
 * @version $Revision$
 * @author Michael Binz
 */
public class ResourceManagerTest
{
    private final ResourceManager _rm =
            ServiceManager.getApplicationService( ResourceManager.class );

    @Resource
    private String stringCountryCode;

    @Before
    public void testInit()
    {
        _rm.injectResources( this );
    }

    /**
     * TODO: As soon as this works in Maven, we
     * can re-enable the resource manager tests.
     */
    @Test
    public void testModulePlacement()
    {
        Module m = ResourceManager.class.getModule();

        assertEquals( "framework.smack", m.getName() );
    }

    @Test
    public void testResourceManagerIdentity()
    {
        ResourceManager fromServiceManager =
                ServiceManager.getApplicationService( ResourceManager.class );

        boolean isIdentical =
                _rm == fromServiceManager;

        assertTrue( isIdentical );
    }

    @Test
    public void testLocalization()
    {
        // Works only on German locale.  Make better.
        assertEquals( "+49", stringCountryCode );
    }
}
