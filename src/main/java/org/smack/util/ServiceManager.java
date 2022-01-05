/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2016 Michael G. Binz
 */
package org.smack.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            new HashMap<>();

    /**
     * Create an instance.
     */
    private ServiceManager()
    {
        throw new AssertionError();
    }

    /**
     * Used in testing.
     */
    public static synchronized void clear()
    {
        _singletons.clear();
    }

    /**
     * Get an application service of the specified type.
     *
     * @param <T> The service type.
     * @param singletonType The class of the application service.
     * @return An instance of the requested service.
     */
    public static synchronized <T> T getApplicationService( Class<T> singletonType )
    {
        if ( ! _singletons.containsKey( singletonType ) )
        {
            try
            {
                _singletons.put(
                        singletonType,
                        ReflectionUtil.createInstanceX( singletonType ) );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }

        return singletonType.cast( _singletons.get( singletonType ) );
    }

    /**
     * Get an application service of the specified type.
     *
     * @param singletonInstance The application service.
     * @param <T> The service type.
     * @return An instance of the requested service.
     */
    public static synchronized <T> T initApplicationService( T singletonInstance )
    {
        @SuppressWarnings("unchecked")
        Class<T> c = (Class<T>)singletonInstance.getClass();

        return initApplicationService( c, singletonInstance );
    }

    /**
     * Initialize an application service with a specific instance.
     *
     * @param <T> The service type.
     * @param clazz The service class.
     * @param singletonInstance The instance to use. The instance has to be assignable
     * to the passed class.
     * @return A service instance.
     */
    public static synchronized <T> T initApplicationService( Class<T> clazz, T singletonInstance )
    {
        for ( Class<?> c : computeClassRange( clazz, singletonInstance.getClass() ) )
        {
            if ( _singletons.containsKey( c ) )
                throw new IllegalArgumentException(
                        "Already initialized: " +
                        _singletons.get( c ) );

            _singletons.put( c, singletonInstance );
        }

        return singletonInstance;
    }

    private static List<Class<?>> computeClassRange(
            Class<?> superclass,
            Class<?> subclass)
    {
        if ( ! isSuperclass( subclass, superclass ) )
            throw new IllegalArgumentException( "Not superclass." );

        ArrayList<Class<?>> result = new ArrayList<>();

        while ( true )
        {
            result.add( subclass );
            if ( subclass.equals( superclass ))
                break;
            subclass = subclass.getSuperclass();
        }

        return result;
    }

    private static boolean isSuperclass(
            Class<?> subclass,
            Class<?> superclass )
    {
        return superclass.isAssignableFrom( subclass );
    }
}
