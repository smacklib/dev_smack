/* $Id$
 *
 * Unpublished work.
 * Copyright © 2016 Michael G. Binz
 */
package org.smack.util;

import java.util.Arrays;

/**
 * An array indexed by an enum type.
 *
 * <V> The array's content type.
 * <K> The enum type.
 *
 * @version $Rev: 1755 $
 * @author Michael Binz
 */
public class EnumArray<K extends Enum<K>, V>
{
    private final V _null;
    private final Object[] _values;

    /**
     * Create an enum array.
     * @param c The enumeration class.
     * @param nullValue The null value to be returned for indices not
     * explicitly set.
     */
    public EnumArray( Class<K> c, V nullValue )
    {
        _values =
                new Object[ c.getEnumConstants().length ];
        _null = nullValue;

        clear();
    }

    /**
     * Set a value at an index.
     * @param idx The index as enumeration element.
     * @param value The value to set.
     */
    public void set( K idx, V value )
    {
        _values[ idx.ordinal() ] = value;
    }

    /**
     * Get the value at index.  If this was not set, then the
     * null value is returned.
     * @param idx The index as enumeration element.
     * @return The value at index.
     */
    @SuppressWarnings("unchecked")
    public V get( K idx )
    {
        return (V)_values[ idx.ordinal() ];
    }

    /**
     * @return The array's size.
     */
    public int size()
    {
        return _values.length;
    }

    /**
     * Clear the array's content.
     */
    public void clear()
    {
        Arrays.fill( _values, _null );
    }

    /**
     * @return The array's null value as passed into the constructor.
     */
    public V getNull()
    {
        return _null;
    }
}
