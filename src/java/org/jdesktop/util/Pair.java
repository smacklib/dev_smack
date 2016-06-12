/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2016 Michael G. Binz
 */
package org.jdesktop.util;

import org.jdesktop.smack.util.JavaUtils;

/**
 * A pair. {@link Pair#equals(Object))} and {@link Pair#hashCode()}
 * are properly overridden.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public final class Pair<T1,T2>
{
    public final T1 a;
    public final T2 b;

    /**
     * Create an instance.
     *
     * @param a One part of the pair.
     * @param b The other part of the pair.
     */
    public Pair( T1 a, T2 b )
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public int hashCode()
    {
        // Josh Bloch alike...
        int ha = a == null ? 0 : a.hashCode();
        int hb = b == null ? 0 : b.hashCode();

        return 37 * ha + hb ^ (hb >>> 16);
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == this )
            return true;

        try
        {
            Pair<?,?> otherPair = (Pair<?, ?>)obj;

            return
                    JavaUtils.equals( otherPair.a, a  ) &&
                    JavaUtils.equals( otherPair.b, b );
        }
        catch ( ClassCastException e )
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return String.format( "[%s:%s]",
                a.toString(),
                b.toString() );
    }
}
