/* $Id: JavaUtils.java 461 2011-01-29 17:20:11Z Michael $
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2011 Michael G. Binz
 */

package de.michab.util;



/**
 * General utilities.
 *
 * @version $Rev: 461 $
 * @author Michael Binz
 */
public class JavaUtils
{
    private JavaUtils()
    {
        throw new AssertionError();
    }



    /**
     * Compare two objects, handle {@code null}.
     *
     * @param a The first object to compare.
     * @param b The second object to compare.
     * @return {@code true} if the objects are equal.  Note that this includes
     * that both parameters were {@code null}.
     */
    public static boolean equals( Object a, Object b )
    {
        if ( a == null )
            return b == null;
        return a.equals( b );
    }
}
