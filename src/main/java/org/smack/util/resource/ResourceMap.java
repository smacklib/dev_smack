/* $Id$
 *
 * Smack Utilities.
 *
 * Copyright © 2017-21 Michael G. Binz
 */
package org.smack.util.resource;

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
    private final Class<?> _class;

    /**
     * Get a the resource map for a class.
     *
     * @param cl The class.
     * @return The corresponding resource map or {@code null} if no
     * resources were found.
     */
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
        Map<String, String> bundle =
                ResourceUtil.preprocessResourceBundle(
                        url, rb );

        String classPrefix =
                _class.getSimpleName() + ".";

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
     * @return The class that this resource map holds resources for.
     */
    public Class<?> getResourceClass()
    {
        return _class;
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
                resolved );
    }
}
