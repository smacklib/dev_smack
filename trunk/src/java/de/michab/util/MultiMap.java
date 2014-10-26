/* $Id$
 *
 * Michael's Utilities
 *
 * Released under Gnu Public License
 * Copyright © 2008-2010 Michael G. Binz
 */
package de.michab.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



/**
 * A double key map.  The map allows to store a single value for key pairs.
 *
 * @version $Revision$
 * @author Michael Binz
 */
public class MultiMap<KT1, KT2, VT>
{
    /**
     * The main map.  This contains for each K1 a secondary map mapping K2 to
     * its corresponding value.
     */
    private final Map<KT1, Map<KT2, VT>> _primaryMap =
        new HashMap< KT1, Map<KT2, VT>>();



    /**
     * Put a value into the map.  No argument must be null.
     *
     * @param pK1 The first key.
     * @param pK2 The second key.
     * @param pValue The value.
     * @throws NullPointerException If one of the arguments was null.
     */
    public VT put( KT1 pK1, KT2 pK2, VT pValue )
    {
        if ( pValue == null )
            throw new NullPointerException( "null value." );

        Map<KT2, VT> secondaryMap;

        if ( _primaryMap.containsKey( pK1 ) )
        {
            // If the key was contained, we get a reference on the
            // existing map.
            secondaryMap = _primaryMap.get( pK1 );
        }
        else
        {
            // If the key was not contained, we create the sub map
            // and add it to our main map.
            secondaryMap = new HashMap<KT2, VT>();
            _primaryMap.put( pK1, secondaryMap );
        }

        return secondaryMap.put( pK2, pValue );
    }



    /**
     * Get a value from the map.
     *
     * @param k1 The primary key.
     * @param k2 The secondary key.
     * @return The value or {@code null} if no value is available for the key
     * pair.
     */
    public VT get( KT1 k1, KT2 k2 )
    {
        if ( ! _primaryMap.containsKey( k1 ) )
            return null;

        Map<KT2, VT> _secondaryMap = _primaryMap.get( k1 );

        if ( ! _secondaryMap.containsKey( k2 ) )
            return null;

        return _secondaryMap.get( k2 );
    }



    /**
     * Get all values for the passed primary key.
     *
     * @param k1 The primary key to look up.
     * @return A map holding the secondary key/value mappings.  This map is
     * empty if none are found.  Null is never returned.
     */
    public Map<KT2, VT> getAll( KT1 k1 )
    {
        if ( ! _primaryMap.containsKey( k1 ) )
            return Collections.emptyMap();

        return Collections.unmodifiableMap( _primaryMap.get( k1 ) );
    }



    /**
     * Get the set of primary keys.
     *
     * @return The set of primary keys.
     */
    public Set<KT1> getPrimaryKeys()
    {
        return Collections.unmodifiableSet( _primaryMap.keySet() );
    }
}
