/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-22 Michael G. Binz
 */
package org.smack.util.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Image;
import java.util.Currency;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.swing.Icon;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smack.util.Pair;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager.Resource;

public final class ResourceManagerTest
{
    private static final ResourceManager _rm =
            ServiceManager.getApplicationService( ResourceManager.class );

    private final static Locale originalDefaultLocale =
            Locale.getDefault();

    @BeforeClass
    public static void onceBeforeAll()
    {
        if ( _rm.getConverter( Currency.class ) == null )
        {
            _rm.addConverter(
                    Currency.class,
                    Currency::getInstance );
        }

        Locale.setDefault( Locale.GERMAN );
    }

    @AfterClass
    public static void onceAfterAll()
    {
        Locale.setDefault( originalDefaultLocale );
    }

    @Before
    public void beforeEach()
    {
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

    @Resource
    private String stringCountryCode;

    @Test
    public void testLocalization()
    {
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

    @Test
    public void testInject_T_1()
    {
        T_BaseClass c = new T_BaseClass();
        _rm.injectResources( c );
        assertEquals( "T_BaseClass", c.name );
    }

    @Test
    public void testInject_T_2()
    {
        T_BaseClass c = new T_SuperClass();
        _rm.injectResources( c );
        assertEquals( "T_Overridden", c.name );
    }

    @Test
    public void testInject_T_3()
    {
        T_SuperClass c = new T_SuperClass();
        _rm.injectResources( c );
        assertEquals( "T_SuperClass", c.name );
        T_BaseClass b = c;
        assertEquals( "T_Overridden", b.name );
    }
}
