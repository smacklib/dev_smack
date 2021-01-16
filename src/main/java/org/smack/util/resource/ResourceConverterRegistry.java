package org.smack.util.resource;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 *
 * @version $Revision$
 * @author Michael Binz
 */
public final class ResourceConverterRegistry
{
    @FunctionalInterface
    public interface Converter<F,T>
    {
        T convert( F f )
            throws Exception;
    }

    public static class DynamicResourceConverter<T> extends ResourceConverter
    {
        private final Converter<String, T> _function;

        DynamicResourceConverter( Class<T> cl, Converter<String,T> f )
        {
            super( cl );

            _function = f;
        }

        @Override
        public Object parseString( String s, ResourceMap r ) throws Exception
        {
            return _function.convert( s );
        }
    }

    private final Logger LOG = Logger.getLogger(
            ResourceConverterRegistry.class.getName() );

    private final HashMap<Class<?>, Converter<String, ?>> _registry =
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
    public <T> void put( Class<T> cl, Converter<String, T> f )
    {
        Objects.requireNonNull( cl );
        Objects.requireNonNull( f );

        if ( _registry.containsKey( cl ) )
            LOG.warning( "Duplicate resource converter for " + cl + "." );

        _registry.put(
                cl,
                f );
    }

    @Deprecated
    public <T> void put( Class<T> cl, ResourceConverter converter )
    {
        @SuppressWarnings("unchecked")
        Converter<String, T> c =
                s -> (T)converter.parseString( s, null );
        put(
                cl,
                c );

    }

    @Deprecated
    public <T> ResourceConverter get( Class<T> cl )
    {
        if ( ! containsKey( cl ) )
            return null;

        return new DynamicResourceConverter( cl, _registry.get( cl ) );
    }

    /**
     * Get a conversion function.
     *
     * @param cl
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Converter<String, T> getConverter( Class<T> cl )
    {
        return (Converter<String,T>)_registry.get( cl );
    }

    @SuppressWarnings("unchecked")
    public <T> T convert( Class<T> cl, String s ) throws Exception
    {
        if ( ! containsKey( cl ) )
            throw new RuntimeException(
                    String.format( "Cannot convert '%s' to %s.", s, cl ) );

        return (T)_registry.get( cl ).convert( s );
    }
}
