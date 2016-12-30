package org.jdesktop.test;

import javax.annotation.Resource;

import org.jdesktop.util.ResourceManager;
import org.jdesktop.util.ServiceManager;

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
