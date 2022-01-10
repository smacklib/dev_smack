/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2016-21 Michael G. Binz
 */
package org.smack.application;

import java.awt.Image;
import java.io.File;

import org.smack.util.ServiceManager;

/**
 * Application information.
 *
 * @author Michael Binz
 */
public class ApplicationContext
{
    private final ApplicationInfo _applicationInfo;

    private final LoggingService _loggingService;

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
        _applicationInfo =
                ServiceManager.getApplicationService( ApplicationInfo.class );
        _loggingService =
                new LoggingService( this );
    }

    public File getHome()
    {
        return _applicationInfo.getHomeDir();
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
        return _applicationInfo.getApplicationClass();
    }

    /**
     * @return The application's id as defined in the resources.
     */
    public String getId()
    {
        return _applicationInfo.getId();
    }

    /**
     * @return The application's title as defined in the resources.
     */
    public String getTitle()
    {
        return _applicationInfo.getTitle();
    }

    /**
     * @return The application's version as defined in the resources.
     */
    public String getVersion()
    {
        return _applicationInfo.getVersion();
    }

    /**
     * @return The application's icon as defined in the resources.
     */
    public Image getIcon()
    {
        return _applicationInfo.getIcon();
    }

    /**
     * @return The application's vendor as defined in the resources.
     */
    public String getVendor()
    {
        return _applicationInfo.getVendor();
    }

    /**
     * @return The application's vendor as defined in the resources.
     */
    public String getVendorId()
    {
        return _applicationInfo.getVendorId();
    }
}
