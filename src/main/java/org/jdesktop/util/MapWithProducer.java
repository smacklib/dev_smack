/* $Id$
 *
 * Released under Gnu Public License
 * Copyright © 2018 Michael G. Binz
 */
package org.jdesktop.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A map that produces content on demand using a factory.
 *
 * @version $Revision: 351 $
 * @author Michael Binz
 */
public class MapWithProducer<K,V>
{
    private final AbstractMap<K,V> _cache;

    private final Function<K, V> _factory;

    /**
     * Create an instance.
     *
     * @param m The hashmap supplier, for example
     * {@code java.util.WeakHashMap<K,V>::new}.
     *
     * @param factory A content factory.
     */
    public MapWithProducer(
            Supplier<AbstractMap<K,V>> m,
            Function<K, V> factory )
    {
        _cache =
                m.get();
        _factory =
                factory;
    }

    /**
     * Create an instance based on a java.util.HashMap.
     *
     * @param factory A content factory.
     */
    public MapWithProducer(
            Function<K, V> factory )
    {
        this( HashMap<K, V>::new , factory );
    }

    public V get( K key )
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
