/* $Id$
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright © 2017 Michael G. Binz
 */
package org.smack.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.smack.util.Pair;
import org.smack.util.ServiceManager;
import org.smack.util.converters.StringConverter;


/**
 * A map holding all resources defined in the resources for
 * the passed class.  Resources for a class foo.bar.Elk are
 * defined in the property file foo.bar.resources.Elk.
 * <p>
 * A property named "color" in the above resource file is found
 * by the key 'color' and the key 'Elk.color'.
 * </p>
 *
 * @author Michael Binz
 */
@SuppressWarnings("serial")
public class ResourceMap extends HashMap<String, String>
{
    private final String _bundleName;

    private final Class<?> _class;

    public static ResourceMap getResourceMap( Class<?> cl )
    {
        Pair<URL, ResourceBundle> crb =
                ResourceUtil.getClassResourcesImpl(
                        Objects.requireNonNull( cl ) );
        if ( crb == null )
            return null;

        return new ResourceMap( cl, crb.left, crb.right );
    }

    private ResourceMap( Class<?> cl, URL url, ResourceBundle rb )
    {
        _class =
                Objects.requireNonNull( cl );
        String simpleName =
                _class.getSimpleName();
        Map<String, String> bundle =
                ResourceUtil.preprocessResourceBundle(
                        url, rb );
        _bundleName =
                cl.getName();
        String classPrefix =
                simpleName + ".";

        for ( String ck : bundle.keySet() )
        {
            String value =
                    bundle.get( ck );

            if ( ck.equals( classPrefix ) )
                throw new AssertionError( "Invalid property name: " + classPrefix );

            put( ck, value );
            if ( ck.startsWith( classPrefix ) )
            {
                put(
                        ck.substring( classPrefix.length() ),
                        value );
            }
            else
            {
                put(
                        classPrefix + ck,
                        value );
            }
        }
    }

    /**
     * @return The name of the underlying resource bundle.
     */
    public String getName()
    {
        return _bundleName;
    }

    /**
     * @return The class loader of the associated class.
     */
    public ClassLoader getClassLoader()
    {
        return _class.getClassLoader();
    }

    /**
     * @return The class that this resource map holds resources for.
     */
    public Class<?> getResourceClass()
    {
        return _class;
    }

    /**
     * @return A stream on the content of the result.
     * @param name The resource name.
     * @throws IOException In case of an error.
     */
    public InputStream getResourceAsStream( String name ) throws IOException
    {
        InputStream result = _class.getClassLoader().getResourceAsStream(
                name );

        if ( result != null )
            return result;

        return
                _class.getModule().getResourceAsStream( name );
    }

    /**
     * Convert the passed key to a target type.
     *
     * @param <T> The expected target type.
     * @param targetType The expected result type.
     * @param key The property key to convert.
     * @return The conversion result.
     */
    public <T> T getAs( String key, Class<T> targetType )
        throws Exception
    {
        String resolved =
                get( key );
        if ( resolved == null )
            throw new IllegalArgumentException( "Key not found: " + key );

        var converter = ServiceManager.getApplicationService(
                StringConverter.class );

        return converter.convert(
                targetType,
                get( key ) );
    }
}
