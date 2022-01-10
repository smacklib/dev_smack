/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2016-2022 Michael G. Binz
 */
package org.smack.application;

import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

import org.smack.util.FileUtil;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;

/**
 * Application information.
 *
 * @author Michael Binz
 */
public class ApplicationInfo
{
    private final Class<?> _applicationClass;

    /**
     * Catches automatic instantiation in ServiceManager, being not
     * allowed for this class.  Instead in {@code main} explicitly initialize
     * the ServiceMenager with an instance of this class.
     */
    public ApplicationInfo()
    {
        throw new IllegalStateException(
           "Init in main: " +
            "ServiceManager.initApplicationService( " +
            "new ApplicationInfo( yourApplication.class ) );" );
    }

    public static final String RESOURCE_KEY_ID = "Application.id";
    public static final String RESOURCE_KEY_TITLE = "Application.title";
    public static final String RESOURCE_KEY_VERSION = "Application.version";
    public static final String RESOURCE_KEY_ICON = "Application.icon";
    public static final String RESOURCE_KEY_VENDOR_ID = "Application.vendorId";
    public static final String RESOURCE_KEY_VENDOR_NAME = "Application.vendor";

    public ApplicationInfo( Class<?> applicationClass )
    {
        _applicationClass =
               Objects.requireNonNull( applicationClass );
        ResourceManager rm =
                ServiceManager.getApplicationService(
                        ResourceManager.class );
        var arm =
                rm.getResourceMap( _applicationClass );

        if ( arm == null )
        {
            var msg = String.format(
                    "Application resources not found."
                    + " Missing 'opens %s;' in module-info.java?",
                    applicationClass.getPackage().getName() );

            throw new IllegalArgumentException( msg );
        }

        id = arm.get(
                RESOURCE_KEY_ID );
        title = arm.get(
                RESOURCE_KEY_TITLE );
        version = arm.get(
                RESOURCE_KEY_VERSION );
        icon = arm.getAs(
                RESOURCE_KEY_ICON, Image.class, null );
        vendor = arm.get(
                RESOURCE_KEY_VENDOR_NAME );
        vendorId = arm.get(
                RESOURCE_KEY_VENDOR_ID );
    }

    public Class<?> getApplicationClass()
    {
        return _applicationClass;
    }

    /**
     * Get the application's home directory.  This is $HOME/.appid.
     *
     * @return The application's home directory.  This is created
     * by this call if it does not exist.
     */
    public File getHome()
    {
        var result = new File(
                FileUtil.getUserHome() + "/." + id );

        if ( result.exists() && result.isDirectory() )
            return result;

        try
        {
            var path = result.toPath();

            if ( result.exists() && !result.isDirectory() )
                Files.delete( path );

            if ( !result.exists() )
                Files.createDirectories( path );

            return result;
        }
        catch ( Exception e )
        {
            throw new RuntimeException(
                    "Could not create home directory: " + result  );
        }
    }

    private final String id;

    /**
     * @return The application's id as defined in the resources.
     */
    public String getId()
    {
        return id;
    }

    private final String title;

    /**
     * @return The application's title as defined in the resources.
     */
    public String getTitle()
    {
        return title;
    }

    private final String version;

    /**
     * @return The application's version as defined in the resources.
     */
    public String getVersion()
    {
        return version;
    }

    private Image icon;

    /**
     * @return The application's icon as defined in the resources.
     */
    public Image getIcon()
    {
        return icon;
    }

    private final String vendor;

    /**
     * @return The application's vendor name as defined in the resources.
     */
    public String getVendor()
    {
        return vendor;
    }

    private final String vendorId;

    /**
     * @return The application's vendor id as defined in the resources.
     */
    public String getVendorId()
    {
        return vendorId;
    }
}
