package org.smack.util.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;

import org.junit.Assert;
import org.junit.Test;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager.Resource;

/**
 *
 * @author Michael
 */
public class ResourceManagerStandardsTest
{
    private final ResourceManager _rm =
            ServiceManager.getApplicationService( ResourceManager.class );

    {
        _rm.injectResources( this );
    }

    @Resource
    private String stringPlain;
    @Resource
    private String stringQuoted;
    @Resource
    private String[] stringArray;

    @Test
    public void testString()
    {
        assertEquals( "true", stringPlain );
        assertEquals( "\"Admiral von Schneider\"", stringQuoted );
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

        Assert.assertArrayEquals( values, stringArray );
    }

    @Resource
    private StringBuilder stringBuilderPlain;
    @Resource
    private StringBuilder stringBuilderQuoted;
    @Resource
    private StringBuilder[] stringBuilderArray;

    @Test
    public void testStringBuilder()
    {
        assertEquals( "true", stringBuilderPlain.toString() );
        assertEquals( "\"Admiral von Schneider\"", stringBuilderQuoted.toString() );
    }

    @Test
    public void testStringBuilderArray()
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

        for ( int i = 0 ; i < values.length ; i++ )
            Assert.assertEquals(
                    values[i],
                    stringBuilderArray[i].toString() );
    }



    @Resource
    private Color color1;
    @Test
    public void testColor()
    {
        assertNotNull( color1 );
        assertEquals( Color.GREEN, color1 );
    }

    @Resource
    private Color color2;
    @Test
    public void testColor2()
    {
        assertNotNull( color2 );
        assertEquals( Color.GREEN, color2 );
        assertEquals( 0xff, color2.getAlpha() );
    }

    @Resource
    private Color color3;
    @Test
    public void testColor3()
    {
        var expected = new Color( 0xff, 0, 0, 0xc1 );
        assertNotNull( color3 );
        assertEquals( expected, color3 );
    }
}
