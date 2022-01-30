/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022 Michael G. Binz
 */
package org.smack.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Encapsulates cached access to a single value,
 *
 * @author Michael G. Binz
 *
 * @param <T> The type of the cached value.
 */
public class CachedHolder<T>
{
    /**
     * The supplier used to create the cached value.
     */
    private final Supplier<T> _supplier;

    /**
     * The cached value;
     */
    private T _value;

    /**
     * A counter for value reads.
     */
    private int _readCount;

    /**
     * Create an instance.
     */
    public CachedHolder( Supplier<T> supplier )
    {
        _supplier = Objects.requireNonNull( supplier );
    }

    /**
     * @return The holder's value.  If the operation is called
     * the first time, the result is requested from the supplier
     * passed into the constructor.
     */
    public T get()
    {
        if ( _readCount == 0 )
            _value = _supplier.get();

        _readCount++;

        return _value;
    }

    @Override
    public String toString()
    {
        return String.format(
                "%s{readCount=%d}",
                getClass().getSimpleName(),
                _readCount );
    }

    /**
     * Reset the holder so that the current value is removed and on the
     * next call to the read operation a new value is requested from
     * the supplier.
     *
     * @return The holder instance.
     */
    public CachedHolder<T> reset()
    {
        _readCount = 0;
        _value = null;
        return this;
    }

    /**
     * @return The current read count.  Used for testing.
     */
    public int getReadCount()
    {
        return _readCount;
    }
}
