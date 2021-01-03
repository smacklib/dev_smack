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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import org.smack.util.JavaUtil;
import org.smack.util.ResourceUtil;
import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;


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

    private final String _resourcePath;

    public ResourceMap( Class<?> cl )
    {
        _class =
                Objects.requireNonNull( cl );
        String simpleName =
                _class.getSimpleName();

        ResourceBundle crb =
                ResourceUtil.getClassResources( cl );

        if ( crb == null )
        {
            _bundleName = StringUtil.EMPTY_STRING;
            _resourcePath = StringUtil.EMPTY_STRING;
            return;
        }

        _bundleName =
                crb.getBaseBundleName();
        JavaUtil.Assert(
                _bundleName.endsWith( simpleName ) );

        _resourcePath =
                _bundleName.substring(
                        0, _bundleName.length() -
                        simpleName.length() ).replace( '.', '/' );

        Map<String, String> bundle =
                ResourceUtil.preprocessResourceBundle(
                        crb );

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
     * Looks up the value for the qualified name. For a key {@code x} and a
     * map for {@code org.jdesktop.Test} the qualified name is {@code Test.x}.
     *
     * @param key The requested key.
     * @return The associated value.
     */
    public Optional<String> getQualified( String key )
    {
        return Optional.ofNullable(
                get( _class.getSimpleName() + "." + key ) );
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
     * @return A resource dir, slash-separated, with a trailing slash.
     * For class org.jdesktop.Test the resource dir
     * is org/jdesktop/resources/. Used for the resolution of
     * secondary resources like icons. If no underlying resource
     * bundle existed, then this is null.
     *
     */
    public String getResourceDir()
    {
        return _resourcePath;
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
    {
        String resolved =
                get( key );
        if ( resolved == null )
            throw new IllegalArgumentException( "Key not found: " + key );

        ResourceManager rm = ServiceManager.getApplicationService(
                ResourceManager.class );

        return rm.convert(
                targetType,
                get( key ),
                this );
    }
}
