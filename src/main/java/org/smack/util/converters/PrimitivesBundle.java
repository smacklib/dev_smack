/**
 * $Id$
 *
 * Unpublished work.
 * Copyright © 2019-21 Michael G. Binz
 */
package org.smack.util.converters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.smack.util.StringUtil;

/**
 * Installs the default converters.
 *
 * @version $Revision$
 * @author Michael Binz
 */
public class PrimitivesBundle extends StringConverterExtension
{
    private static ImageIcon imageIconFromUrl( String s ) throws Exception
    {
        var url = new URL( s );

        try ( var urlStream = url.openStream() )
        {
            // Note that the URL-based constructor is not used because
            // it always succeeds, even in cases of a wrong URL.
            return new ImageIcon( urlStream.readAllBytes() );
        }
    }

    private static Icon iconFromUrl( String s ) throws Exception
    {
        return imageIconFromUrl( s );
    }

    private static Image imageFromUrl( String s ) throws Exception
    {
        return imageIconFromUrl( s ).getImage();
    }

    @Override
    public void extendTypeMap( StringConverter registry )
    {
        // Primitives

        registry.put( boolean.class, Boolean::parseBoolean );
        registry.put( Boolean.class, Boolean::parseBoolean );
        registry.put( byte.class, Byte::decode );
        registry.put( Byte.class, Byte::decode );
        registry.put( short.class, Short::decode );
        registry.put( Short.class, Short::decode );
        registry.put( int.class, Integer::decode );
        registry.put( Integer.class, Integer::decode );
        registry.put( long.class, Long::decode );
        registry.put( Long.class, Long::decode );
        registry.put( float.class, Float::parseFloat );
        registry.put( Float.class, Float::parseFloat );

        registry.put( double.class, Double::parseDouble );
        registry.put( Double.class, Double::parseDouble );
        // char ...

        // The remaining converters supported by default.
        registry.put(
                String.class,
                s -> { return s; } );
        registry.put(
                String[].class,
                StringUtil::splitQuoted );
        registry.put(
                Font.class,
                Font::decode );
        registry.put(
                Image.class,
                PrimitivesBundle::imageFromUrl );
        registry.put(
                Icon.class,
                PrimitivesBundle::iconFromUrl );
        registry.put( Color.class,
                ConverterUtils::parseColor );
    }
}
