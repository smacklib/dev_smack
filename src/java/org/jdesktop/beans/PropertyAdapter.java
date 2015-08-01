/* $Id$
 *
 * Laboratory.
 *
 * Released under Gnu Public License
 * Copyright © 2011 Michael G. Binz
 */

package org.jdesktop.beans;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import org.jdesktop.smack.util.ReflectionUtils;




/**
 * Wraps an object that offers a property change listener interface.
 * An object supports this if it offers the operations
 * {@code addPropertyChangeListener( PropertyChangeListener p )} and
 * {@code removePropertyChangeListener( PropertyChangeListener p )}.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class PropertyAdapter
{
    /**
     * The target object.
     */
    private Object _bean;

    /**
     * The add operation.
     */
    private final Method _addPcl;

    /**
     * The remove operation.
     */
    private final Method _removePcl;

    /**
     * The add operation.
     */
    private final Method _addPclNamed;

    /**
     * The remove operation.
     */
    private final Method _removePclNamed;

    /**
     * Creates an instance.  The constructor validates the contract
     * for adaptable classes and fails with an exception if the passed
     * object does not follow.
     *
     * @param bean The object to adapt.
     */
    public PropertyAdapter( Object bean )
    {
        _bean = bean;

        Class<?> beanClass = bean.getClass();

        try
        {
            _addPcl =
                beanClass.getMethod(
                        "addPropertyChangeListener",
                        PropertyChangeListener.class );
            _addPclNamed =
                beanClass.getMethod(
                        "addPropertyChangeListener",
                        String.class, PropertyChangeListener.class );
            _removePcl =
                beanClass.getMethod(
                        "removePropertyChangeListener",
                        PropertyChangeListener.class );
            _removePclNamed =
                beanClass.getMethod(
                        "removePropertyChangeListener",
                        String.class, PropertyChangeListener.class );
        }
        catch ( SecurityException e )
        {
            throw new IllegalArgumentException( e );
        }
        catch ( NoSuchMethodException e )
        {
            throw new IllegalArgumentException( e );
        }
    }



    /**
     * Adds a <code>PropertyChange</code> listener. Containers and attached
     * components use these methods to register interest in this
     * <code>Action</code> object. When its enabled state or other property
     * changes, the registered listeners are informed of the change.
     *
     * @param listener  a <code>PropertyChangeListener</code> object
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        ReflectionUtils.invokeQuiet(
                _addPcl,
                _bean,
                listener );
    }



    /**
     * Removes a <code>PropertyChange</code> listener.
     *
     * @param listener  a <code>PropertyChangeListener</code> object
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        ReflectionUtils.invokeQuiet(
                _removePcl,
                _bean,
                listener );
    }

    /**
     * Adds a <code>PropertyChange</code> listener. Containers and attached
     * components use these methods to register interest in this
     * <code>Action</code> object. When its enabled state or other property
     * changes, the registered listeners are informed of the change.
     *
     * @param propertyName The name of the property.
     * @param listener  a <code>PropertyChangeListener</code> object
     */
    public void addPropertyChangeListener( String propertyName, PropertyChangeListener listener)
    {
        ReflectionUtils.invokeQuiet(
                _addPclNamed,
                _bean,
                propertyName,
                listener );
    }

    /**
     * Removes a <code>PropertyChange</code> listener.
     *
     * @param propertyName The name of the property.
     * @param listener  a <code>PropertyChangeListener</code> object
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener( String propertyName, PropertyChangeListener listener)
    {
        ReflectionUtils.invokeQuiet(
                _removePclNamed,
                _bean,
                propertyName,
                listener );
    }
}
