/**
 * $Id$
 *
 * Unpublished work.
 * Copyright © 2019 Michael G. Binz
 */
package org.smack.util.converters;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class ConverterUtils
{
    /**
     * Parses colors with an alpha channel and comma separated RGB[A] values.
     * Legal formats for color resources are:
     * "#RRGGBB",  "#AARRGGBB", "R, G, B", "R, G, B, A"
     * or the color plain names defined on {@link Color}.
     * @author Romain Guy
     */
    static Color parseColor(String s) throws Exception
    {
        final Color result;

        if (s.startsWith("#")) {
            switch (s.length()) {
                // RGB
                case 7:
                    result = Color.decode(s);
                    break;
                // ARGB
                case 9:
                    int alpha = Integer.decode(s.substring(0, 3));
                    int rgb = Integer.decode("#" + s.substring(3));
                    result = new Color(alpha << 24 | rgb, true);
                    break;
                default:
                    throw new Exception("invalid #RRGGBB or #AARRGGBB color string: " + s);
            }
        } else {
            result = checkPlainColorName( s );
        }

        if ( result == null )
            throw new Exception("Use #RRGGBB or #AARRGGBB color string or plain color name: " + s);

        return result;
    }

    private static Color checkPlainColorName( String name )
    {
        try
        {
            Field f = Color.class.getField( name );
            if ( ! Color.class.equals( f.getType() ) )
                return null;
            if ( ! Modifier.isStatic( f.getModifiers() ) )
                return null;
            return (Color) f.get( null );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
