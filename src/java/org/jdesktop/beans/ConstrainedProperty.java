/* $Id$
 *
 * Laboratory.
 *
 * Released under Gnu Public License
 * Copyright © 2011 Michael G. Binz
 */

package org.jdesktop.beans;

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;

/**
 * One instance of this type is placed in the client properties
 * of each target component.  It acts as a wrapper for the target
 * adding a constrained enabled Java Bean property.
 *
 * @version $Rev$
 * @author Michael Binz
 */
@SuppressWarnings("serial")
public class ConstrainedProperty<T>
    extends VetoableChangeSupport
{
    private final PropertyProxy<T,?> _target;

    /**
     * Create an instance for the given target.
     */
    public ConstrainedProperty( PropertyProxy<T,?> target )
    {
        super( target );
        _target = target;
    }

    public void set( T what )
        throws PropertyVetoException
    {
        T old = _target.get();
        fireVetoableChange( _target.getName(), old, what );
        _target.set( what );
    }
}
