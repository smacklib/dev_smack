package org.jdesktop.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.jdesktop.smack.util.StringUtils;

import javafx.util.Pair;

/**
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class ResourceManager
{
    private final static Logger LOG =
            Logger.getLogger( ResourceManager.class.getName() );

    private Map<Class<?>,ResourceConverter> _converters =
            new HashMap<>();

    public ResourceManager()
    {
        for ( ResourceConverter c : ServiceLoader.load( ResourceConverter.class ) )
        {
            Class<?> type =  c.getType();

            if ( _converters.containsKey( type ) )
                LOG.warning( "Duplicate resource converter for: " + type );

            _converters.put( type, c );
        }
    }

    public void injectResources( Object o )
    {
        if ( o instanceof Class )
            injectResources( null, (Class<?>)o );
        else
            injectResources( o, o.getClass() );
    }

    public void injectResources( Object instance, Class<?> cIass )
    {
        List<Pair<Field, Resource>> fields = ReflectionUtil.getAnnotatedFields(
                cIass,
                Resource.class,
                instance == null ? Modifier.STATIC : 0 );

        if ( fields.isEmpty() )
            return;

        ResourceMap rb =
                new ResourceMap( cIass );

        if ( rb.isEmpty() )
        {
            // @Resource annotations exist, but no property file.
            LOG.severe( "No resources found for class " + cIass.getName() );
            return;
        }

        for ( Pair<Field, Resource> c : fields )
        {
            Field f = c.getKey();
            Resource r = c.getValue();

            String name = r.name();

            if ( StringUtils.isEmpty( name ) )
                name = f.getName();

            String value = rb.get( name );

            if ( value == null )
            {
                LOG.severe( "No resource definition found for field " + f.getName() );
                continue;
            }

            try
            {
                performInjection( instance, f, value, rb );
            }
            catch ( Exception e )
            {
                LOG.log( Level.SEVERE, "Injection failed for field " + f.getName(), e );
            }
        }
    }

    /**
     *
     * @param instance
     * @param f
     * @param value
     * @param map
     * @throws Exception
     */
    private void performInjection(
            Object instance,
            Field f,
            String value,
            ResourceMap map ) throws Exception
    {
        boolean accessible = f.isAccessible();

        try
        {
            if ( ! accessible )
                f.setAccessible( true );

            performInjectionImpl( instance, f, value, map );
        }
        catch ( Exception e )
        {
            String msg =
                    String.format(
                            "Resource init for %s failed.",
                            f.getName() );

            LOG.log(
                    Level.WARNING, msg, e );
        }
        finally
        {
            if ( f.isAccessible() != accessible )
                f.setAccessible( accessible );
        }
    }

    /**
     *
     * @param instance
     * @param f
     * @param resource
     * @param map
     * @throws Exception
     */
    private void performInjectionImpl(
            Object instance,
            Field f,
            String resource,
            ResourceMap map ) throws Exception
    {
        Class<?> targetType = f.getType();

        ResourceConverter converter =
                _converters.get( targetType );

        if ( converter != null )
        {
            f.set(
                    instance,
                    converter.parseString( resource, map ) );

            return;
        }

        // Check if we can synthesize an array resource converter.
        if ( targetType.isArray() )
        {
            converter = _converters.get( targetType.getComponentType() );
            if ( converter != null )
            {
                converter = new ArrayResourceConverter( converter, targetType );
                f.set( instance, converter.parseString( resource, map ) );
                return;
            }
        }

        // Check if we can synthesize a constructor-based resource converter.
        if ( targetType.getConstructor( String.class ) != null )
        {
            converter = new ConstructorResourceConverter(
                    targetType.getConstructor( String.class ),
                    targetType );

            f.set( instance, converter.parseString( resource, map ) );

            return;
        }

        LOG.warning( "No resource converter found for type: " + targetType );
    }

    private static class ArrayResourceConverter extends ResourceConverter
    {
        private final ResourceConverter _delegate;

        ArrayResourceConverter( ResourceConverter delegate, Class<?> type )
        {
            super( type );

            if ( ! type.isArray() )
                throw new IllegalArgumentException();

            _delegate = delegate;
        }

        @Override
        public Object parseString( String s, ResourceMap r )
                throws Exception
        {
            String[] split = StringUtils.splitQuoted( s );

            Object result = Array.newInstance(
                    getType().getComponentType(), split.length );

            int idx = 0;
            for ( String c : split )
                Array.set( result, idx++, _delegate.parseString( c, r ) );

            return result;
        }
    }

    private static class ConstructorResourceConverter extends ResourceConverter
    {
        private final Constructor<?> _ctor;

        ConstructorResourceConverter( Constructor<?> delegate, Class<?> type )
        {
            super( type );

            _ctor = delegate;
        }

        @Override
        public Object parseString( String s, ResourceMap r )
            throws Exception
        {
            return _ctor.newInstance( s );
        }
    }
}
