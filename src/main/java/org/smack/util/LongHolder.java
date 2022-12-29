/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2016-22 Michael G. Binz
 */
package org.smack.util;

import java.util.Objects;

/**
 * A long holder.
 *
 * @author MICBINZ
 */
public class LongHolder
{
    private long value;

    /**
     * Create an initialized instance.
     *
     * @param v The initial value.
     */
    public LongHolder( long v )
    {
        value = v;
    }

    /**
     * Create a zero-initialized instance.
     */
    public LongHolder()
    {
        this( 0L );
    }

    /**
     * Set the holder's value.
     *
     * @param v The value to set.
     */
    public synchronized void set( long v )
    {
        value = v;
    }

    /**
     * @return The holder's value.
     */
    public synchronized long get()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return Objects.toString( value );
    }
}
