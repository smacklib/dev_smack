/* $Id$
 *
 * Laboratory.
 *
 * Released under Gnu Public License
 * Copyright © 2011 Michael G. Binz
 */
package org.jdesktop.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.jdesktop.smack.util.ReflectionUtils;


/**
 * Allows to set a JavaBean property in a relatively simple way.  An
 * instance if this class represents the property on the target object
 * in a (runtime) type-safe way.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class PropertyProxy<T>
{
    /**
     * The property descriptor of the proxied property.
     */
    private final PropertyDescriptor _targetProperty;



    /**
     * The target object.
     */
    private final Object _targetObject;



    /**
     * Create an instance of the proxy and ensures that the property is
     * actually available on the target.  'Available' means that the
     * class of the target object follows the Java Beans conventions.
     *
     * @param propName The name of the property.
     * @param target The target object. Null not allowed.
     * @throws IllegalArgumentException of the property is not supported
     * by the target object.
     */
    public PropertyProxy( String propName, Object target )
    {
        try
        {
            _targetProperty = new PropertyDescriptor( propName, target.getClass() );

            if ( _targetProperty.getReadMethod() == null )
                throw new IllegalArgumentException( "target has no read method" );
            if ( _targetProperty.getWriteMethod() == null )
                throw new IllegalArgumentException( "target has no write method" );

            _targetObject = target;
        }
        catch ( IntrospectionException e )
        {
            throw new IllegalArgumentException( e );
        }
    }



    /**
     * Get the property's value.
     *
     * @return The property's value.
     */
    @SuppressWarnings("unchecked")
    public T get()
    {
        return (T)ReflectionUtils.invokeQuiet(
                _targetProperty.getReadMethod(),
                _targetObject );
    }



    /**
     * Set the property's value.
     *
     * @param value The value to set.
     */
    public void set( T value )
    {
        ReflectionUtils.invokeQuiet(
                _targetProperty.getWriteMethod(),
                _targetObject,
                value );
    }



    /**
     * Get the property name.
     *
     * @return The property name.
     */
    public String getName()
    {
        return _targetProperty.getName();
    }



    /**
     * Returns a string representation of an instance for debug purposes.
     *
     * @return A string representation of an instance for debug purposes.
     */
    @Override
    public String toString()
    {
        StringBuilder result =
            new StringBuilder( _targetObject.getClass().getSimpleName() );
        return result
            .append( '@' )
            .append( _targetProperty.getName() )
            .toString();
    }
}
