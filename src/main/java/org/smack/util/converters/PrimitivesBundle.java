package org.smack.util.converters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceConverterExtension;
import org.smack.util.resource.ResourceConverterRegistry;

/**
 * Installs ResourceConverters for primitive types.
 *
 * @version $Revision$
 * @author Michael Binz
 */
public class PrimitivesBundle extends ResourceConverterExtension
{

    private static Icon iconFromUrl( String s ) throws MalformedURLException
    {
        return new ImageIcon( new URL( s ) );
    }

    private static Image imageFromUrl( String s ) throws MalformedURLException
    {
        return new ImageIcon( new URL( s ) ).getImage();
    }

    @Override
    public void extendTypeMap( ResourceConverterRegistry registry )
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
                StringBuilder.class,
                StringBuilder::new );
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
                ColorStringConverter::parse );

    }
}
