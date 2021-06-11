/* $Id$
 *
 * Copyright © 2014 Michael G. Binz
 */
package org.smack.util.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Resource Bundle helpers.
 *
 * @version $Rev$
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
     * RuntimeException if the resource was not found.
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
     * Forbid instantiation.
     */
    private ResourceUtil()
    {
        throw new AssertionError();
    }
}
