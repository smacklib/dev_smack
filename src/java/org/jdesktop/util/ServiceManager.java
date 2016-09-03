/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2016 Michael G. Binz
 */
package org.jdesktop.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Management of ApplicationServices.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public final class ServiceManager
{
    /**
     * The map of singular application services.
     */
    private static final Map<Class<?>, Object> _singletons =
            new HashMap<Class<?>, Object>();

    /**
     * Create an instance.
     */
    public ServiceManager()
    {
        // Catch ctor.
    }

    /**
     * Get an application service of the specified type.
     *
     * @param singletonType The type of the application service.
     * @return An instance of the requested service.
     */
    public static synchronized <T> T getApplicationService( Class<T> singletonType )
    {
        if ( !  _singletons.containsKey( singletonType ) )
            try
            {
                _singletons.put(
                        singletonType,
                        singletonType.newInstance() );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }

        return singletonType.cast( _singletons.get( singletonType ) );
    }


    /**
     * Get an application service of the specified type.
     *
     * @param singletonType The type of the application service.
     * @return An instance of the requested service.
     */
    public static synchronized <T> T initApplicationService( T singletonInstance )
    {
        @SuppressWarnings("unchecked")
        Class<T> c = (Class<T>)singletonInstance.getClass();

        return initApplicationService( c, singletonInstance );
    }

    /**
     * Get an application service of the specified type.
     *
     * @param singletonType The type of the application service.
     * @return An instance of the requested service.
     */
    public static synchronized <T> T initApplicationService( Class<T> clazz, T singletonInstance )
    {
        if ( ! clazz.isAssignableFrom( singletonInstance.getClass() ) )
            throw new ClassCastException();

        _singletons.put( clazz, singletonInstance );

        return singletonInstance;
    }
}
