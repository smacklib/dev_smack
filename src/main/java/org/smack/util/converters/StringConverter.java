/**
 * $Id$
 *
 * Unpublished work.
 * Copyright © 2021 Michael G. Binz
 */
package org.smack.util.converters;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smack.util.ReflectionUtil;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;

/**
 * A string converter offering conversions services for strings to arbitrary
 * types.
 *
 * @version $Revision$
 * @author Michael Binz
 */
public final class StringConverter
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
            StringConverter.class.getName() );

    private final HashMap<Class<?>, Converter<String, ?>> _registry =
            new HashMap<>();

    public StringConverter()
    {
        LOG.setLevel( Level.WARNING );

        for ( StringConverterExtension c : ServiceLoader.load( StringConverterExtension.class ) )
            c.extendTypeMap( this );

        for ( ResourceConverter c : ServiceLoader.load( ResourceConverter.class ) )
            put( c.getType(), c );
    }

    public boolean containsKey( Class<?> cl )
    {
        return getConverter( cl ) != null;
    }

    /**
     * @param converter A converter to add to the list of known converters.
     */
    public <T> void put( Class<T> cl, Converter<String, T> f )
    {
        Objects.requireNonNull( cl );
        Objects.requireNonNull( f );

        LOG.info( "Adding rc for: " + cl );

        // Directly ask the has table.  The outbound containsKey
        // triggers creation of entries.
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
        return (Converter<String,T>)_registry.computeIfAbsent( cl, this::synthesize );
    }

    @SuppressWarnings("unchecked")
    public <T> T convert( Class<T> cl, String s ) throws Exception
    {
        if ( ! containsKey( cl ) )
            throw new RuntimeException(
                    String.format( "Cannot convert '%s' to %s.", s, cl ) );

        return (T)_registry.get( cl ).convert( s );
    }

    private <T> Converter<String, T> synthesizeEnum( Class<T> cl )
    {
        LOG.info( "Synthesize enum for: " + cl );

        return s -> {
            // Handle enums.
            for (var c : cl.getEnumConstants()) {
                if (c.toString().equalsIgnoreCase(s)) {
                    return c;
                }
            }

            // Above went wrong, generate a good message.
            List<String> allowed = new ArrayList<>();
            for (var c : cl.getEnumConstants()) {
                allowed.add(c.toString());
            }

            String message = String.format(
                    "Unknown enum value: '%s'.  Allowed values are %s.",
                    s,
                    StringUtil.concatenate(", ", allowed));

            throw new IllegalArgumentException(message);
        };
    }

    private <T> Converter<String, T> synthesizeStringCtor( Class<T> cl, Constructor<T> ctor )
    {
        LOG.info( "Synthesize string ctor for: " + cl );
        return s -> ctor.newInstance( s );
    }

    @SuppressWarnings("unchecked")
    private <T> Converter<String, T> synthesizeArray( Class<T> cl )
    {
        LOG.info( "Synthesize array for: " + cl );

        var componentConverter =
                getConverter( cl.getComponentType() );

        return s -> {
            String[] split = StringUtil.splitQuoted( s );

            var result = Array.newInstance(
                    cl.getComponentType(), split.length );

            int idx = 0;
            for ( String c : split )
            {
                Array.set(
                        result,
                        idx++,
                        componentConverter.convert( c ) );
            }

            return (T)result;
        };
    }

    /**
     * Sythesizes missing converters.
     *
     * @param <T>
     * @param cl
     * @return
     */
    private <T> Converter<String,T> synthesize( Class<T> cl )
    {
        if ( cl.isEnum() )
            return synthesizeEnum( cl );

        if ( cl.isArray() && getConverter( cl.getComponentType() ) != null )
            return synthesizeArray( cl );

        var stringCtor = ReflectionUtil.getConstructor( cl, String.class );
        if ( stringCtor != null )
            return synthesizeStringCtor( cl, stringCtor );

        return null;
    }
}
