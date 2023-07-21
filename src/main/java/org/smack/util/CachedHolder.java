/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2022-2023 Michael G. Binz
 */
package org.smack.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Encapsulates cached access to a single value.
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
    private Supplier<T> _supplier;

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
        if ( _supplier != null )
        {
            _value = _supplier.get();
            _supplier = null;
        }

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
     * @return The current read count.  Used for testing.
     */
    public int getReadCount()
    {
        return _readCount;
    }
}
