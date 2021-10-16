/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-21 Michael G. Binz
 */
package org.smack.util;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

/**
 * A supplier that uses the passed class' default constructor to
 * create an instance.
 *
 * @param <T> The supplier's result type.
 * @author Michael Binz
 */
public class ConstructorSupplier<T>
    implements Supplier<T>
{
    private final Constructor<T> _ctor;

    public ConstructorSupplier( Class<T> cl )
    {
        try {
            _ctor = cl.getDeclaredConstructor();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(
                    "No default constructor.",
                    e );
        }

    }

    @Override
    public T get()
    {
        try {
            return _ctor.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
