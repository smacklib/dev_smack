/* $Id: 8cd524cc25dff00d9a1984a06ded0b560757345b $
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2012-17 Michael G. Binz
 */
package org.jdesktop.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Collection utilities.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class UtilCollection
{
    /**
     * Creates the intersection of the passed sets.
     *
     * @param a The first operand for the intersection.
     * @param b The second operand for the intersection.
     * @param <T> The collection element type.
     * @return A newly allocated set containing the intersections of
     * the passed sets.
     */
    public static <T> Set<T> intersection( Set<T> a, Set<T> b )
    {
        Set<T> result = new HashSet<>( a );
        result.retainAll( b );
        return result;
    }

    /**
     * Convert a collection to an array.
     * @param p The collection to convert.
     * @param <T> The collection element type.
     * @param clazz The element type.
     * @return A newly allocated array of element type clazz.
     */
    @SuppressWarnings("unchecked")
    static <T> T[] toArray( Collection<T> p, Class<T> clazz )
    {
        return p.toArray(
                (T[]) Array.newInstance( clazz, p.size() ) );
    }

    /**
     * @param list A list.
     * @param <T> The collection element type.
     * @return The last element in the passed list. May be empty
     * if the list was empty.
     */
    public static <T> Optional<T> lastElement( List<T> list )
    {
        if ( list.isEmpty() )
            return Optional.empty();

        return Optional.of(
                list.get(
                        list.size()-1 ) );
    }

    /**
     * Test if an array is empty.
     *
     * @param array The array to test. {@code null} is allowed.
     * @param <T> The array element type.
     * @return {@code true} if the array is not null and has a length greater
     * than zero.
     */
    public static <T> boolean isEmptyArray( T[] array )
    {
        return array == null || array.length == 0;
    }

    private UtilCollection()
    {
        throw new AssertionError();
    }
}
