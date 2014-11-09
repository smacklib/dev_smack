/* $Id$
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright (c) 2002-2003 Michael G. Binz
 */
package de.michab.mack.util;

import java.util.Hashtable;
import java.lang.ref.*;



/**
 * <p>Implements a map useful for implementing flyweight factories.  Maps a key
 * to the actual flyweight instance that has to be returned.  The flyweight
 * instances are referred by a weak reference.  Entries from the map will be
 * removed as soon as the last strong reference to the flyweight instance
 * is removed.</p>
 * <p>As a consequence this can be used for holding flyweights even if the domain
 * is basically unbound, like for example the domain of all integers.<p>
 * The class does intentionally not inherit from any of the collection
 * interfaces to keep its implementation lean.</p>
 *
 * @author Michael G. Binz
 */
public class WeakValueMap<K,V>
{
  /**
   * The weak link that is kept to the values.
   */
  static private class WeakValue<K,V>
    extends WeakReference<V>
  {
    /**
     * The key associated with the value referred by an instance of this class.
     */
    private K key;



    /**
     * Create a weak reference to the value.
     *
     * @param k The key.
     * @param v The value associated to the key.
     * @param q The reference queue used for key cleanup.
     */
    private WeakValue( K k, V v, ReferenceQueue q )
    {
      super( v, q );
      key = k;
    }
  }



  /**
   * Reference queue for cleared values.
   */
  private ReferenceQueue<WeakValue> _queue =
    new ReferenceQueue<WeakValue>();



  /**
   * The hashtable holding the actual references.
   */
  private Hashtable<K,WeakValue<K,V>> _hashtable = 
    new Hashtable<K,WeakValue<K,V>>();



  /**
   * Updates this map so that the given <code>key</code> maps to the given
   * <code>value</code>.  Neither <code>key</code> nor <code>value</code> must
   * be <code>null</code>.
   *
   * @param  key    The key that is to be mapped to the given
   *                <code>value</code>.
   * @param  value  The value to which the given <code>key</code> is to be
   *                mapped.
   * @throws IllegalArgumentException In case one of the passed arguments was
   *         <code>null</code>.
   */
  public synchronized void put( K key, V value )
  {
    clearQueue();

    // TODO it may be possible to allow a null value.  That would mean an
    // explicit remove of an entry from the map.  Currently not needed.
    if ( null == key || null == value )
      throw new IllegalArgumentException( "No <null> key or value allowed." );

    _hashtable.put( key, new WeakValue<K,V>( key, value, _queue ) );
  }



  /**
   * Access an entry of the map.
   *
   * @param key The entry's key.
   * @return A reference to the entry or <code>null</code> if the entry has not
   *         been found.
   */
  public synchronized V get( K key )
  {
    clearQueue();

    WeakValue<K,V> wv = _hashtable.get( key );

    if ( wv == null )
      return null;

    return wv.get();
  }



  /**
   * Remove all invalidated entries from the map, that is, remove all entries
   * whose values have been discarded.
   */
  private void clearQueue()
  {
    WeakValue wv;

    while ((wv = (WeakValue)_queue.poll()) != null)
      _hashtable.remove( wv.key );
  }
}
