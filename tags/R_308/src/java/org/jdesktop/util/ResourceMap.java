/* $Id$
 *
 * Utilities
 *
 * Released under Gnu Public License
 * Copyright © 2017 Michael G. Binz
 */
package org.jdesktop.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import org.jdesktop.smack.util.ResourceUtils;


/**
 * A map holding all resources defined in the resources for
 * the passed class.  Resources for a class foo.bar.Elk are
 * defined in the property file foo.bar.resources.Elk.
 * <p>
 * A property named "color" in the above resource file is found
 * by the key 'color' and the key 'Elk.color'.
 * </p>
 *
 * @version $Rev$
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
                ResourceUtils.getClassResources( cl );

        if ( crb == null )
        {
            _bundleName = StringUtil.EMPTY_STRING;
            _resourcePath = StringUtil.EMPTY_STRING;
            return;
        }

        _bundleName =
                crb.getBaseBundleName();

        assert _bundleName.endsWith( simpleName );

        _resourcePath =
                _bundleName.substring(
                        0, _bundleName.length() -
                        simpleName.length() ).replace( '.', '/' );

        Map<String, String> bundle =
                ResourceUtils.preprocessResourceBundle(
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
     * Get the name of the underlying resource bundle.
     */
    public String getName()
    {
        return _bundleName;
    }

    public ClassLoader getClassLoader()
    {
        return _class.getClassLoader();
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
}
