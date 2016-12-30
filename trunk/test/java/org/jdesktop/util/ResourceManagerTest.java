package org.jdesktop.util;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;

/**
 *
 * @author Michael
 */
public class ResourceManagerTest
{
    @Resource
    private String stringResource;
    @Resource
    private String[] stringArrayResource;

    @Resource
    private byte byteResource;
    @Resource
    private short shortResource;
    @Resource
    private int intResource;
    @Resource
    private long longResource;

    @Resource
    private float floatResource;
    @Resource
    private double doubleResource;

    @Resource
    private String stringCountryCode;

    @Override
    public String toString()
    {
        return stringResource;
    }

    @Resource
    private boolean booleanResource;

    @Test
    public void testPrimitiveBoolean()
    {
        assertEquals( 0, 0 );
    }

    public static void main( String[] args )
    {
        ResourceManager rm =
                ServiceManager.getApplicationService( ResourceManager.class );

        ResourceManagerTest test =
                new ResourceManagerTest();

        rm.injectResources( test );

        System.out.println( test.toString() );
    }
}
