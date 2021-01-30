/* $Id$
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright © 2017 Michael G. Binz
 */
package org.smack.util.resource;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smack.util.ReflectionUtil;
import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.collections.WeakMapWithProducer;
import org.smack.util.converters.StringConverter;
import org.smack.util.converters.StringConverter.Converter;

/**
 * A ResourceManager.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class ResourceManager
{
    private final static Logger LOG =
            Logger.getLogger( ResourceManager.class.getName() );

    /**
     * The Resource annotation marks a field that is injected
     * by the ResourceManager.
     * If a field is annotated and no definition is found in
     * the property file then this is an error.  If a deflt
     * value is provided then this is used and no error is
     * signaled.
     */
    @Target({FIELD})
    @Retention(RUNTIME)
    public @interface Resource {
        /**
         * @return The name of the resource.  For field annotations,
         * the default is the field name.
         */
        String name() default StringUtil.EMPTY_STRING;

        /**
         * @return A default value for this resource.
         */
        String dflt() default DFLT_NULL;
    }

    private final StringConverter _converters =
            ServiceManager.getApplicationService( StringConverter.class );

    private WeakHashMap<Class<?>, Boolean> staticInjectionDone =
            new WeakHashMap<>();

    private final WeakMapWithProducer<Class<?>, ResourceMap> _resourceMapCache =
            new WeakMapWithProducer<>( ResourceMap::getResourceMap );

    /**
     * Create an instance.  Commonly done via the ServiceManager.
     */
    public ResourceManager()
    {
    }

    /**
     * @param converter A converter to add to the list of known converters.
     */
    public <T> void addConverter( Class<T> cl, Converter<String, T> f )
    {
        _converters.put(
                cl,
                f );
    }

    /**
     * @param converter A converter to add to the list of known converters.
     */
    public <T> Converter<String, T> getConverter( Class<T> cl )
    {
        return _converters.getConverter( cl );
    }

    /**
     * Inject the passed bean's properties from the passed map. The prefix is
     * used to find the configuration keys in the map. Keys in the
     * map have to look like prefix.propertyName. The dot is added to
     * the prefix.
     *
     * @param bean The bean whose properties are injected.
     * @param prefix The prefix used to filter the map's keys.
     * @param map Inject the passed bean's properties from this map.
     */
    public void injectProperties( Object bean, String prefix, ResourceMap map )
    {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(
                    bean.getClass() );
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException( "Introspection failed.", e );
        }

        // Add the dot.
        prefix += ".";

        Set<String> definedKeys = new HashSet<>();
        for ( String c : map.keySet() )
            if ( c.startsWith( prefix ) )
                definedKeys.add( c );

        if ( definedKeys.size() == 0 )
            return;

        for ( PropertyDescriptor c : beanInfo.getPropertyDescriptors() )
        {
            Method setter = c.getWriteMethod();

            // Skip read-only properties.
            if ( setter == null )
                continue;

            String currentKey = prefix + c.getName();
            if ( ! definedKeys.contains( currentKey ) )
                continue;

            definedKeys.remove( currentKey );

            try
            {
                setter.invoke(
                        bean,
                        convert(
                                c.getPropertyType(),
                                map.get( currentKey ) ) );
            }
            catch ( InvocationTargetException e )
            {
                throw new RuntimeException( e.getCause() );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }

            if ( definedKeys.size() == 0 )
                return;
        }

        for ( String c : definedKeys )
            LOG.warning( String.format(
                    "Key '%s' defined in map does not match property.", c ) );
    }


    public void injectResources( Object o )
    {
        if ( o instanceof Class )
            injectResources( null, (Class<?>)o );
        else
            injectResources( o, o.getClass() );
    }

    /**
     * @param clazz The class for which a resource map is requested.
     * @return The resource map for the passed class.
     */
    public ResourceMap getResourceMap( Class<?> cl )
    {
        return _resourceMapCache.get( cl );
    }

    /**
     * @param clazz The class for which a resource map is requested.
     * @return The resource map for the passed class. An empty
     * map if no resources were defined.
     */
    public Map<String,String> getResourceMap2( Class<?> cl )
    {
        var result =_resourceMapCache.get( cl );
        if ( result != null )
            return result;

        return Collections.emptyMap();
    }

    public void injectResources( Object instance, Class<?> cl )
    {
        if ( instance == null && staticInjectionDone.containsKey( cl ) )
            return;

        var map =
                getResourceMap2( cl );
        // Note that it may be valid that map is empty, as long
        // as all @Resources offer a dflt value.

        ReflectionUtil.processAnnotation(
                Resource.class,
                cl::getDeclaredFields,
                s -> {
                    if ( instance == null )
                        return Modifier.isStatic( s.getModifiers() );
                    return true;
                },
                (f, r) -> {

                    if ( Modifier.isStatic( f.getModifiers() ) &&
                            staticInjectionDone.containsKey( cl ) )
                        return;

                    String name = r.name();

                    if ( StringUtil.isEmpty( name ) )
                        name = String.format(
                                "%s.%s",
                                cl.getSimpleName(),
                                f.getName() );

                    String value = map.get( name );

                    // If we got no value, get the Resource default
                    // definition.
                    if ( value == null )
                    {
                        value = getDefaultField( r );
                        // If the resource default definition is set to
                        // the empty string this means not to touch the field.
                        if ( StringUtil.EMPTY_STRING.equals( value ) )
                            return;
                    }

                    // If no value found bail out.
                    if ( value == null )
                    {
                        var msg = String.format(
                                "No resource key found for field '%s#%s'.",
                                f.getDeclaringClass(),
                                f.getName() );
                        throw new MissingResourceException(
                                msg,
                                f.getDeclaringClass().toString(),
                                name);
                    }

                    try
                    {
                        performInjection( instance, f, value );
                    }
                    catch ( Exception e )
                    {
                        var msg = "Injection failed for field " + f.getName();
                        LOG.log( Level.SEVERE, msg, e );
                        throw new RuntimeException( msg );
                    }
                } );

        staticInjectionDone.put( cl, Boolean.TRUE );
    }

    /**
     *
     * @param instance
     * @param f
     * @param resource
     * @param map
     * @throws Exception
     */
    private void performInjection(
            Object instance,
            Field f,
            String resource ) throws Exception
    {
        Class<?> targetType = f.getType();

        try
        {
            if ( ! f.canAccess( instance ) )
                f.setAccessible( true );

            f.set(
                    instance,
                    convert( targetType, resource ) );
        }
        catch ( Exception e )
        {
            throw new Exception( String.format(
                    "Injecting %s: %s",
                    f.toString(),
                    e.getMessage() ),
                    e );
        }
    }

    <T> T convert( Class<T> targetType, String toConvert )
        throws Exception
    {
        var converter =
                _converters.getConverter( targetType );

        if ( converter == null )
            throw new IllegalArgumentException(
                    "No resource converter found for type: " + targetType );

        try
        {
            return converter.convert( toConvert );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot convert '%s' to %s.",
                            toConvert,
                            targetType.getName() ),
                    e );
        }
    }

    /**
     * Used as a default null value for the @Resource.dflt field. Never
     * modify.
     */
    static final private String DFLT_NULL = "313544b196b54c29a26e43bdf204b023";

    /**
     * Get the normalized value of the annotation's dflt field.
     * @param r The annotation reference.
     * @return The normalized value which includes null if not set.
     */
    private static String getDefaultField( Resource r )
    {
        var result = r.dflt();

        if ( DFLT_NULL.equals( result ))
            return null;

        return result;
    }
}
