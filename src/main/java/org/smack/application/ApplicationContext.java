/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2016-21 Michael G. Binz
 */
package org.smack.application;

import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

import org.smack.util.FileUtil;
import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceManager;

/**
 * Application information.
 *
 * @author Michael Binz
 */
public class ApplicationContext
{
    private final Class<?> _applicationClass;

    private final File _applicationHome;
    private final LoggingService _loggingService;

    public static ApplicationContext init( Class<?> applicationClass )
    {
        return ServiceManager.initApplicationService(
                new ApplicationContext( applicationClass ) );
    }

    public static ApplicationContext get()
    {
        return ServiceManager.getApplicationService(
                ApplicationContext.class );
    }

    /**
     * Catches automatic instantiation in ServiceManager, being not
     * allowed for this class.  Instead in {@code main} explicitly initialize
     * the ServiceMenager with an instance of this class.
     */
    public ApplicationContext()
    {
        throw new IllegalStateException( "Init ServiceManager in main." );
    }

    public ApplicationContext( Class<?> applicationClass )
    {
        _applicationClass =
               Objects.requireNonNull( applicationClass );
        ResourceManager rm =
                ServiceManager.getApplicationService(
                        ResourceManager.class );
        var arm =
                rm.getResourceMap2( _applicationClass );
        var SC =
                String.class;
        id = arm.getAs(
                "Application.id",
                SC,
                _applicationClass.getSimpleName() );
        title = arm.getAs(
                "Application.title",
                SC,
                id );
        version = arm.getAs(
                "Application.version",
                SC,
                "0.0.0" );
        icon = arm.getAs(
                "Application.icon",
                Image.class,
                (Image)null );
        vendor = arm.getAs(
                "Application.vendor",
                SC,
                StringUtil.EMPTY_STRING );
        vendorId = arm.getAs(
                "Application.vendorId",
                SC,
                StringUtil.EMPTY_STRING );

        _applicationHome =
                createHomeDir( id );
        _loggingService =
                new LoggingService( this );
    }

    public File getHome()
    {
        return _applicationHome;
    }

    /**
     * Get the home directory for the passed application id.  This is
     * $HOME/.appid.
     *
     * @param groupId An group id.
     * @return The respective log directory.  This directory
     * is created by this call if it does not exist.
     */
    static File createHomeDir( String groupId )
    {
        try
        {
            var result = new File(
                    FileUtil.getUserHome() + "/." + groupId );

            if ( result.exists() && ! result.isDirectory() )
                Files.delete( result.toPath() );

            if ( !result.exists() )
                Files.createDirectories( result.toPath() );

            return result;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return The application log directory.
     */
    public File getLogDir()
    {
        return _loggingService.getLogDir();
    }

    /**
     * @return The application class.
     */
    public Class<?> getApplicationClass()
    {
        return _applicationClass;
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
     * @return The application's vendor as defined in the resources.
     */
    public String getVendor()
    {
        return vendor;
    }

    private final String vendorId;

    /**
     * @return The application's vendor as defined in the resources.
     */
    public String getVendorId()
    {
        return vendorId;
    }
}
