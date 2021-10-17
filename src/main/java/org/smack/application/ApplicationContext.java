/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2016-21 Michael G. Binz
 */
package org.smack.application;

import java.awt.Image;
import java.util.Objects;

import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceManager;

/**
 * Application information.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class ApplicationContext
{
    private final Class<?> _applicationClass;

    private final LoggingService _loggingService;

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
                _applicationClass::getSimpleName );
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

        _loggingService =
                new LoggingService( this );
    }

    public Class<?> getApplicationClass()
    {
        return _applicationClass;
    }

    private final String id;

    /**
     * Return the application's id as defined in the resources.
     * @return The application's id.
     */
    public String getId()
    {
        return id;
    }

    private final String title;

    /**
     * Return the application's title as defined in the resources.
     * @return The application's title.
     */
    public String getTitle()
    {
        return title;
    }

    private final String version;

    /**
     * Return the application's version as defined in the resources.
     * @return The application's version.
     */
    public String getVersion()
    {
        return version;
    }

    private Image icon;

    /**
     * Return the application's icon as defined in the resources.
     * @return The application icon.
     */
    public Image getIcon()
    {
        return icon;
    }

    private final String vendor;

    /**
     * Return the application's vendor as defined in the resources.
     * @return The vendor name.
     */
    public String getVendor()
    {
        return vendor;
    }

    private final String vendorId;

    /**
     * Return the application's vendor as defined in the resources.
     * @return The vendor name.
     */
    public String getVendorId()
    {
        return vendorId;
    }
}
