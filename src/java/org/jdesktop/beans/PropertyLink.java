/* $Id: PropertyLink.java 462 2011-01-29 17:22:22Z Michael $
 *
 * Laboratory.
 *
 * Released under Gnu Public License
 * Copyright © 2011 Michael G. Binz
 */

package org.jdesktop.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jdesktop.smack.util.JavaUtils;




/**
 * Links a bound property on a source object to a bound property on
 * a target object.
 *
 * @version $Rev: 462 $
 * @author Michael Binz
 */
public class PropertyLink
{
    private final String _propertySrcName;

    private PropertyProxy<Object> _targetProperty;



    /**
     * Creates a property update link between the source and target.
     * The property is expected to exist on both objects.
     *
     * @param propName
     * @param source
     * @param target
     */
    public PropertyLink(
            Object source,
            String propName,
            Object target )
    {
        _propertySrcName = propName;

        PropertyAdapter pa =
            new PropertyAdapter( source );

        pa.addPropertyChangeListener( _listener );

        _targetProperty =
            new PropertyProxy<Object>( propName, target );
    }



    /**
     * Creates a property update link between the source and target.
     *
     * @param propName
     * @param source
     * @param target
     */
    public PropertyLink(
            Object source,
            String propSrcName,
            Object target,
            String propTgtName )
    {
        _propertySrcName = propSrcName;

        PropertyAdapter pa =
            new PropertyAdapter( source );

        pa.addPropertyChangeListener( _listener );

        _targetProperty =
            new PropertyProxy<Object>( propTgtName, target );
    }



    /**
     * A listener for source changes.
     */
    private PropertyChangeListener _listener = new PropertyChangeListener()
    {
        @Override
        public void propertyChange( PropertyChangeEvent evt )
        {
            // Ignore change events for other properties.
            if ( ! _propertySrcName.equals( evt.getPropertyName() ) )
                return;

            Object newValue = evt.getNewValue();

            // If the new value and the value on the target are already the
            // same we ignore the call.
            if ( JavaUtils.equals( _targetProperty.get(), newValue ) )
                return;

            // Set the value.
            _targetProperty.set( newValue );
        }
    };
}
