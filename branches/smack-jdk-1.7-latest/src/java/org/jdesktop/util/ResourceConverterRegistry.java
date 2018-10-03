package org.jdesktop.util;

import java.util.HashMap;

/**
 *
 *
 * @version $Revision$
 * @author Michael Binz
 */
public final class ResourceConverterRegistry
{
    private final HashMap<Class<?>, ResourceConverter> _registry =
            new HashMap<>();

    public ResourceConverterRegistry()
    {
    }

    public boolean containsKey( Class<?> cl )
    {
        return _registry.containsKey( cl );
    }

    public void put( Class<?> cl, ResourceConverter converter )
    {
        _registry.put( cl, converter );
    }

    public ResourceConverter get( Class<?> cl )
    {
        return _registry.get( cl );
    }
}
