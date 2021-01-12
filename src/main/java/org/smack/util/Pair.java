package org.smack.util;

import java.util.Objects;

public class Pair<L,R>
{
    public final R right;
    public final L left;

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
}
