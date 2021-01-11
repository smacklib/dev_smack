package org.smack.util.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Image;
import java.util.Currency;

import javax.swing.Icon;

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
        if ( _rm.getConverter( Currency.class ) == null )
        {
            _rm.addConverter(
                    Currency.class,
                    Currency::getInstance );
        }

        _rm.injectResources( this );
    }

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

    @Resource
    private Image image;

    @Test
    public void testImage()
    {
        assertNotNull( image );
    }

    @Resource
    private Icon icon;

    @Test
    public void testIcon()
    {
        assertNotNull( icon );
    }

    @Resource
    private Currency currency;

    @Test
    public void testCurrency()
    {
        assertNotNull( currency );
    }
}
