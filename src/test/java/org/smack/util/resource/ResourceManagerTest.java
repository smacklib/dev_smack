package org.smack.util.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Image;
import java.util.Currency;
import java.util.MissingResourceException;

import javax.swing.Icon;

import org.junit.Before;
import org.junit.Test;
import org.smack.util.Pair;
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
        assertEquals( 33, image.getHeight( null ) );
    }

    @Resource
    private Icon icon;

    @Test
    public void testIcon()
    {
        assertNotNull( icon );
        assertEquals( 33, icon.getIconHeight() );
    }

    @Resource
    private Currency currency;

    @Test
    public void testCurrency()
    {
        assertNotNull( currency );
    }

    static class ResourcesDefault
    {
        @Resource( dflt = "string" )
        public String string;

        @Resource( dflt = "313" )
        public int integer;
    }

    @Test
    public void testDefaultInjection()
    {
        ResourcesDefault res = new ResourcesDefault();
        assertNull( res.string );
        assertEquals( 0, res.integer );
        _rm.injectResources( res );
        assertEquals( "string", res.string );
        assertEquals( 313, res.integer );
    }

    static class ResourcesRequired
    {
        @Resource()
        public String string;
    }

    @Test
    public void testRequiredInjection()
    {
        ResourcesRequired res = new ResourcesRequired();
        assertNull( res.string );
        try
        {
            _rm.injectResources( res );
            fail();
        }
        catch ( MissingResourceException expected )
        {
        }
    }

    @Test
    public void testGetResourceMap()
    {
        var map = _rm.getResourceMap( ResourceManagerTest.class );

        assertNotNull( map );
        assertNotNull( "TTD", map.get( "currency" ) );
    }

    @Test
    public void testGetResourceMapFailBootstrap()
    {
        var map = _rm.getResourceMap( String.class );
        assertNull( map );
    }

    @Test
    public void testGetResourceMapFail()
    {
        var map = _rm.getResourceMap( Pair.class );
        assertNull( map );
    }
}
