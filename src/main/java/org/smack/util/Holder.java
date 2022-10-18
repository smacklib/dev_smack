/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2016-22 Michael G. Binz
 */
package org.smack.util;

import java.util.Objects;

/**
 * A generic holder class.
 *
 * @param <T>
 *
 * @author MICBINZ
 */
public class Holder<T>
{
    private T value;

    /**
     * Create an initialized instance.
     * @param v The initial value.
     */
    public Holder( T v )
    {
        value = v;
    }

    /**
     * Set the holder's value.
     * @param v The value to set.
     * @return
     */
    public void set( T v )
    {
        value = v;
    }

    /**
     * @return The holder's value.
     */
    public T get()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return Objects.toString( value );
    }
}
