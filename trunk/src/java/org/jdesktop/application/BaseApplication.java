/* $Id: ConstrainedProperty.java 600 2012-09-03 18:58:30Z Michael $
 *
 * Smack Application.
 *
 * Released under Gnu Public License
 * Copyright © 2015 Michael G. Binz
 */
package org.jdesktop.application;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.jdesktop.beans.AbstractBeanEdt;

/**
 *
 * @version $Rev$
 * @author Michael Binz
 */
class BaseApplication extends AbstractBeanEdt
{
    private Map<Class<?>, Object> _singletons =
            new HashMap<Class<?>, Object>();

    /**
     *
     */
    public BaseApplication()
    {
        // Catch ctor.
    }

    /**
     *
     * @param type
     * @return
     */
    public synchronized <T> T getApplicationService(Class<T> singletonType)
    {
        if ( !  _singletons.containsKey( singletonType ) )
            try
            {
                _singletons.put(
                        singletonType,
                        makeSingletonInstance( singletonType ) );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }

        return singletonType.cast( _singletons.get( singletonType ) );
    }

    private <T> Object makeSingletonInstance( Class<T> singletonType )
        throws Exception
    {
        // First check if we have a constructor accepting an application instance.
        Constructor<T> ctor = getConstructor( singletonType, Application.class );
        if ( ctor != null )
            return ctor.newInstance( this );

        // Fall back to the default constructor.
        return singletonType.newInstance();
    }

    private <T> Constructor<T> getConstructor( Class<T> klass, Class<?> ... parameterTypes  )
    {
        try
        {
            return klass.getDeclaredConstructor( parameterTypes );
        }
        catch ( Exception e )
        {
            // Ignored.
        }

        return null;
    }
}
