/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2019-21 Michael G. Binz
 */
package org.smack.util;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A supplier that uses a constructor to create an object instance.
 *
 * @param <T> The supplier's result type.
 * @author Michael Binz
 */
public class ConstructorSupplier<T>
    implements Supplier<T>
{
    private final Constructor<T> _ctor;
    private final Object[] _arguments;

    /**
     * Create the supplier.
     *
     * @param cl The class of the supplier's result.
     * @param as The arguments that are passed to the constructor if
     * the supplier's result is requested.
     */
    public ConstructorSupplier( Class<T> cl, Object ... as )
    {
        _arguments = as;

        var cs =
                Stream.of( _arguments ).map(
                        e -> e.getClass() ).toArray(Class<?>[]::new);
        try
        {
            _ctor = cl.getDeclaredConstructor(cs);
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
        try
        {
            return _ctor.newInstance( _arguments );
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
