package org.smack.util.resource;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 *
 *
 * @version $Revision$
 * @author Michael Binz
 */
public final class ResourceConverterRegistry
{
    private final Logger LOG = Logger.getLogger(
            ResourceConverterRegistry.class.getName() );

    private final HashMap<Class<?>, ResourceConverter> _registry =
            new HashMap<>();
    private final HashMap<Class<?>, Function<String, ?>> _registry2 =
            new HashMap<>();

    public ResourceConverterRegistry()
    {
    }

    public boolean containsKey( Class<?> cl )
    {
        return _registry.containsKey( cl );
    }

    /**
     * @param converter A converter to add to the list of known converters.
     */
    public <T> void put( Class<T> cl, Function<String, T> f )
    {
        _registry2.put(
                Objects.requireNonNull( cl ),
                Objects.requireNonNull( f ) );

        if ( _registry.containsKey( cl ) )
            LOG.warning( "Duplicate resource converter for " + cl + "." );

        var dynamicConverter = new DynamicResourceConverter<T>( cl, f );

        _registry.put(
                dynamicConverter.getType(),
                dynamicConverter );

    }

    @Deprecated
    public void put( Class<?> cl, ResourceConverter converter )
    {
        _registry2.put(
                Objects.requireNonNull( cl ),
                Objects.requireNonNull(
                        s -> {
                            try
                            {
                                return converter.parseString( s, null );
                            }
                            catch ( Exception e )
                            {
                                throw new RuntimeException( e );
                            }
                        } ) );

        _registry.put(
                Objects.requireNonNull( cl ),
                Objects.requireNonNull( converter ) );
    }

    @Deprecated
    public ResourceConverter get( Class<?> cl )
    {
        return _registry.get( cl );
    }

    @SuppressWarnings("unchecked")
    public <T> T convert( Class<T> cl, String s )
    {
        if ( ! containsKey( cl ) )
            throw new RuntimeException(
                    String.format( "Cannot convert '%s' to %s.", s, cl ) );

        return (T)_registry2.get( cl ).apply( s );
    }
}
