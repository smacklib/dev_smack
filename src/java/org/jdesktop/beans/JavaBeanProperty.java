/* $Id$
 *
 * Laboratory.
 *
 * Released under Gnu Public License
 * Copyright © 2015 Michael G. Binz
 */
package org.jdesktop.beans;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jdesktop.smack.util.JavaUtils;

/**
 * Support class for implementing java bean properties on components.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class JavaBeanProperty<T> implements PropertyType<T>
{
    private String _name;
    private T _value;
    private Component _host;
    private Class<T> _type;

    public JavaBeanProperty( Component host, T initialValue, String propertyName )
    {
        // This internally validates if the host offers the required
        // set and get operations.
        _type = new PropertyProxy<T>( propertyName, host ).getType();

        _host = host;
        _value = initialValue;
        _name = propertyName;
    }

    @Override
    public void set( T newValue )
    {
        if ( JavaUtils.equals( _value, newValue ) )
            return;

        PropertyChangeListener[] pcls =
                _host.getPropertyChangeListeners( _name );

        if ( ! JavaUtils.isEmptyArray( pcls ) )
        {
            T oldValue = _value;

            PropertyChangeEvent evt =
                    new PropertyChangeEvent( _host, _name, oldValue, _value );

            for ( PropertyChangeListener c : pcls )
                c.propertyChange( evt );
        }

        _value = newValue;
    }

    @Override
    public T get()
    {
        return _value;
    }

    @Override
    public Class<T> getType()
    {
        return _type;
    }

    @Override
    public String getName()
    {
        return _name;
    }
}
