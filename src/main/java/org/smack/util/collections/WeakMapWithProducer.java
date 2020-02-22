/* $Id$
 *
 * Released under Gnu Public License
 * Copyright © 2017 Michael G. Binz
 */
package org.smack.util.collections;

import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * A weak map that produces content on demand using a factory.
 *
 * @version $Revision$
 * @author Michael Binz
 */
public class WeakMapWithProducer<K,V>
    extends MapWithProducer<K, V>
{
    public WeakMapWithProducer( Function<K, V> factory )
    {
        super(
            WeakHashMap::new,
            factory );
    }
}
