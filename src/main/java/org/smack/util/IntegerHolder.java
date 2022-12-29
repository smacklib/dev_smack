/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2016-22 Michael G. Binz
 */
package org.smack.util;

import java.util.Objects;

/**
 * An integer holder.
 *
 * @author MICBINZ
 */
public class IntegerHolder
{
    private int value;

    /**
     * Create an initialized instance.
     *
     * @param v The initial value.
     */
    public IntegerHolder( int v )
    {
        value = v;
    }

    /**
     * Create a zero-initialized instance.
     */
    public IntegerHolder()
    {
        this( 0 );
    }

    /**
     * Set the holder's value.
     *
     * @param v The value to set.
     */
    public void set( int v )
    {
        value = v;
    }

    /**
     * @return The holder's value.
     */
    public int get()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return Objects.toString( value );
    }
}
