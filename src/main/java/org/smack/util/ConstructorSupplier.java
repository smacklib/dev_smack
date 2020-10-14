/* $Id$
 *
 * Released under Gnu Public License
 * Copyright © 2019 Michael G. Binz
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
    private final Class<T> _class;

    public ConstructorSupplier( Class<T> claß )
    {
        _class = claß;
    }

    @Override
    public T get()
    {
        try {
            Constructor<T> c =
                    _class.getDeclaredConstructor();
            return c.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
