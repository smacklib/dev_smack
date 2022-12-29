/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2016-22 Michael G. Binz
 */
package org.smack.util;

import java.util.Objects;

/**
 * A holder for double types.
 *
 * @author MICBINZ
 */
public class DoubleHolder
{
    private double value;

    /**
     * Create an initialized instance.
     *
     * @param v The initial value.
     */
    public DoubleHolder( double v )
    {
        value = v;
    }

    /**
     * Create a zero-initialized instance.
     */
    public DoubleHolder()
    {
        this( 0.0d );
    }

    /**
     * Set the holder's value.
     *
     * @param v The value to set.
     */
    public synchronized void set( double v )
    {
        value = v;
    }

    /**
     * @return The holder's value.
     */
    public synchronized double get()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return Objects.toString( value );
    }
}
