/*
 * Smack Java @ https://github.com/smacklib/dev_smack
 *
 * Copyright © 2014-2022 Michael G. Binz
 */
package org.smack.util.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Resource Bundle helpers.
 *
 * @author Michael Binz
 */
public final class ResourceUtil
{
    /**
     * Load a named resource from the resource package of the passed
     * class.
     *
     * @param cl The class used to locate the resource package.
     * @param name The name of the resource.
     * @return The resource content. Never null, throws a
     * {@link RuntimeException} if the resource was not found.
     */
    public static byte[] loadResource(
            Class<?> cl,
            String name )
    {
        try ( InputStream is = cl.getResourceAsStream( name ) )
        {
            if ( is == null )
                throw new RuntimeException(
                        "Resource not found: " + name );

            return is.readAllBytes();
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( name, e );
        }
    }

    /**
     * Load a named properties file from the passed class' resource position.
     *
     * @param cl The class defining the resource position.
     * @param name The name of the properties file.
     * @return The read properties.
     * @throws Exception If the file was not found.
     */
    public static Properties loadProperties( Class<?> cl, String name ) throws Exception
    {
        var resource =
                ResourceUtil.loadResource( cl, name );

        try ( var stream = new ByteArrayInputStream( resource ) )
        {
            Properties result = new Properties();
            result.load( stream );
            return result;
        }
    }

    /**
     * Forbid instantiation.
     */
    private ResourceUtil()
    {
        throw new AssertionError();
    }
}
