/**
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2021 Michael G. Binz
 */
package org.smack.util.converters;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smack.util.ReflectionUtil;
import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;

/**
 * Offers conversions services for strings to arbitrary
 * types.
 *
 * @version $Revision$
 * @author Michael Binz
 */
public final class StringConverter
{
    private final Logger LOG = Logger.getLogger(
            StringConverter.class.getName() );

    @FunctionalInterface
    public interface Converter<F,T>
    {
        T convert( F f )
            throws Exception;
    }

    private final HashMap<Class<?>, Converter<String, ?>> _registry =
            new HashMap<>();

    /**
     * Create an instance.  Use with {@link ServiceManager} to get the common
     * instance.
     */
    public StringConverter()
    {
        LOG.setLevel( Level.WARNING );

        // If this is an unnamed module, for example when loaded from
        // an executable jar, then we fall back to classic ServiceLoader
        // plug-in resolution.
        LOG.info( "Named:" + StringConverter.class.getModule().isNamed() );

        for ( StringConverterExtension c : ServiceLoader.load( StringConverterExtension.class ) )
            c.extendTypeMap( this );
    }

    /**
     * Check if a converter for the passed class is available.
     * @param cl The class to convert to.
     * @return true if a converter is available.
     */
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

        // Directly ask the hash table.  The outbound containsKey
        // triggers creation of entries.
        if ( _registry.containsKey( cl ) )
            LOG.warning( "Duplicate resource converter for " + cl + "." );

        _registry.put(
                cl,
                f );
    }

    /**
     * Get a conversion function.
     *
     * @param cl The conversion target class.
     * @return A converter function or null if none is available.
     */
    @SuppressWarnings("unchecked")
    public <T> Converter<String, T> getConverter( Class<T> cl )
    {
        return (Converter<String,T>)_registry.computeIfAbsent( cl, this::synthesize );
    }

    /**
     * Convert a string to a target class.
     * @param <T> The target type.
     * @param cl The target type's class.
     * @param s The string to convert.
     * @return The conversion result.  This may be null, depending
     * on the converter.
     * @throws IllegalArgumentException In case of conversion failure.
     */
    @SuppressWarnings("unchecked")
    public <T> T convert( Class<T> cl, String s )
    {
        if ( ! containsKey( cl ) )
            throw new IllegalArgumentException(
                    "No resource converter found for type: " + cl );
        try
        {
            return (T)_registry.get( cl ).convert( s );
        }
        catch ( IllegalArgumentException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot convert '%s' to %s: %s",
                            s,
                            cl.getName(),
                            e.getMessage()),
                    e );
        }
    }

    private <T> Converter<String, T> synthesizeEnum( Class<T> cl )
    {
        LOG.info( "Synthesize enum for: " + cl );
        return s -> ReflectionUtil.getEnumElement( cl, s );
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
     * Synthesizes missing converters.
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
