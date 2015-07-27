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
    private final Method _addPropertyChangeListener;

    /**
     * The remove operation.
     */
    private final Method _removePropertyChangeListener;



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
            _addPropertyChangeListener =
                beanClass.getMethod(
                        "addPropertyChangeListener",
                        PropertyChangeListener.class );
            _removePropertyChangeListener =
                beanClass.getMethod(
                        "removePropertyChangeListener",
                        PropertyChangeListener.class );
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
                _addPropertyChangeListener,
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
                _removePropertyChangeListener,
                _bean,
                listener );
    }
}
