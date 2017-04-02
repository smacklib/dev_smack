/* $Id$
 *
 * Smack Application.
 *
 * Released under Gnu Public License
 * Copyright © 2015 Michael G. Binz
 */
package org.jdesktop.application;

import org.jdesktop.beans.AbstractBeanEdt;
import org.jdesktop.util.ServiceManager;

/**
 * A raw base application.  Offers management of ApplicationServices.
 *
 * @version $Rev$
 * @author Michael Binz
 */
class BaseApplication extends AbstractBeanEdt
{
    /**
     * Create an instance.
     */
    public BaseApplication()
    {
        // Catch ctor.
    }

    /**
     * Get an application service of the specified type.
     *
     * @param singletonType The type of the application service.
     * @return An instance of the requested service.
     */
    public synchronized <T> T getApplicationService( Class<T> singletonType )
    {
        return ServiceManager.getApplicationService( singletonType );
    }
}
