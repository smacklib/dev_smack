/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022-2024 Michael G. Binz
 */
package org.smack.util;

import java.util.Objects;

/**
 * A general-purpose pair.
 *
 * @param <L>
 * @param <R>
 */
public final class Pair<L,R>
{
    public final R right;
    public final L left;

    /**
     * Create an instance.
     *
     * @param l The left element.
     * @param r The right element.
     */
    public Pair( L l, R r )
    {
        right = r;
        left = l;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o == this )
            return true;
        if ( o == null )
            return false;
        if ( Pair.class != o.getClass() )
            return false;

        Pair<?, ?> otherPair = (Pair<?, ?>)o;

        return
                Objects.equals( right, otherPair.right ) &&
                Objects.equals( left, otherPair.left );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(
                right != null ? right : Boolean.TRUE,
                left != null ? left : Boolean.TRUE );
    }

    @Override
    public String toString()
    {
        return String.format( "%s{%s, %s}",
                getClass().getSimpleName(),
                Objects.toString( left ),
                Objects.toString( right ));
    }
}
