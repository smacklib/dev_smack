/* $Id: 893703b794bf058429a6203bfc4c98c14e5bfbe8 $
 *
 * Unpublished work.
 * Copyright © 2018 Michael G. Binz
 */
package org.jdesktop.util.converters;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

import javafx.scene.image.Image;

public class FxImageConverter extends ResourceConverter
{
    static private final Logger LOG =
            Logger.getLogger( FxImageConverter.class.getName() );

    public FxImageConverter()
    {
        super( Image.class );
    }

    /**
     * If path doesn't have a leading "/" then the resourcesDir
     * is prepended, otherwise the leading "/" is removed.
     */
    private static String resourcePath(final String path, ResourceMap resourceMap) {
        if (path == null) {
            return null;
        } else if (path.startsWith("/")) {
            return (path.length() > 1) ? path.substring(1) : null;
        } else {
            return resourceMap.getResourceDir() + path;
        }
    }

    @Override
    public Object parseString( String s, ResourceMap r ) throws Exception
    {
        URL url = r.getClassLoader().getResource(
                resourcePath( s, r ) );

        // Loading the image using the Image( URL ) ctor is not
        // working when used in a OneJar-jar-file, thus the
        // workaround using the stream.
        try ( InputStream is = url.openStream() )
        {
            Image result = new Image( is );

            if ( result.isError() )
                LOG.log( Level.SEVERE, "Image error.", result.getException() );

            return result;
        }
    }
}
