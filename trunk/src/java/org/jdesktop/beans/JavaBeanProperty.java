/* $Id$
 *
 * Laboratory.
 *
 * Released under Gnu Public License
 * Copyright © 2015 Michael G. Binz
 */
package org.jdesktop.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.jdesktop.smack.util.JavaUtils;
import org.jdesktop.smack.util.ReflectionUtils;

/**
 * Support class for implementing java bean properties on components.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class JavaBeanProperty<T,B> implements PropertyType<T,B>
{
    private static final Logger LOG =
            Logger.getLogger( JavaBeanProperty.class.getName() );

    private String _name;
    private T _value;
    private B _host;
    private Class<T> _type;

//    private final List<PropertyChangeListener> _pcls;

    public JavaBeanProperty( B host, T initialValue, String propertyName )
    {
        // This internally validates if the host offers the required
        // set and get operations.
        _type = new PropertyProxy<T,B>( propertyName, host ).getType();

        _host = host;
        _value = initialValue;
        _name = propertyName;

//        _pcls = Collections.unmodifiableList( getPcls( host, propertyName ) );
    }

    @Override
    public void set( T newValue )
    {
        if ( JavaUtils.equals( _value, newValue ) )
            return;

        T oldValue = _value;
        _value = newValue;

        List<PropertyChangeListener> pcls = getPcls( _host, _name );
        if ( pcls.size() > 0 )
        {
            PropertyChangeEvent evt =
                    new PropertyChangeEvent( _host, _name, oldValue, newValue );

            for ( PropertyChangeListener c : pcls )
                c.propertyChange( evt );
        }
    }

    private List<PropertyChangeListener> getPcls( Object bean, String propertyName )
    {
        String gpcls = "getPropertyChangeListeners";

        Method getPclsNamed =
                ReflectionUtils.getMethod( bean.getClass(), gpcls, String.class );

        if ( null == getPclsNamed )
            throw new IllegalArgumentException( "Not found: " + bean.getClass().getName() + "#" + gpcls );

        try
        {
            List<PropertyChangeListener> result = new ArrayList<PropertyChangeListener>();

            result.addAll(
                    Arrays.asList(
                            (PropertyChangeListener[])getPclsNamed.invoke(
                                    bean,
                                    propertyName ) ) );

            return result;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
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

    @Override
    public B getBean()
    {
        return _host;
    }
}
