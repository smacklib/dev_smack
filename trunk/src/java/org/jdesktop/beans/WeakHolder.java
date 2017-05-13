/* $Id$
 *
 * Released under Gnu Public License
 * Copyright © 2017 Michael G. Binz
 */
package org.jdesktop.beans;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A weak hashmap that holds n objects for a single key.
 * This is designed to link weak JavaFX property bindings
 * against their user objects.
 *
 * @version $Revision$
 * @author Michael Binz
 */
class WeakHolder
{
    private WeakHashMap<Object, Set<Object>> _delegate =
            new WeakHashMap<>();

    public void put( Object anchor, Object anchored )
    {
        Set<Object> existing = _delegate.get( anchor );
        if ( existing == null )
            existing = new HashSet<>();
        existing.add( anchored );
        _delegate.put( anchor, existing );
    }
}
