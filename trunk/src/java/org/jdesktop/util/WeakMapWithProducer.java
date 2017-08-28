/* $Id$
 *
 * Released under Gnu Public License
 * Copyright © 2017 Michael G. Binz
 */
package org.jdesktop.util;

import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * A weak map that produces content on demand using a factory.
 *
 * @version $Revision$
 * @author Michael Binz
 */
public class WeakMapWithProducer<K,V>
{
    private final WeakHashMap<K, V> _cache =
            new WeakHashMap<>();

    private final Function<K, V> _factory;

    public WeakMapWithProducer( Function<K, V> factory )
    {
        _factory = factory;
    }

    V get( K key )
    {
        V result = _cache.get( key );

        if ( result == null )
        {
            result = _factory.apply( key );
            _cache.put( key, result );
        }

        return result;
    }
}
