package org.smack.util.converters;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

public class ColorStringConverter extends ResourceConverter
{
    public ColorStringConverter() {
        super(Color.class);
    }

    /**
     * Parses colors with an alpha channel and comma separated RGB[A] values.
     * Legal formats for color resources are:
     * "#RRGGBB",  "#AARRGGBB", "R, G, B", "R, G, B, A"
     * or the color plain names defined on {@link Color}.
     * @author Romain Guy
     */
    @Override
    public Object parseString(String s, ResourceMap ignore) throws Exception {

        // Implanted michab.
        {
            Color result = checkPlainColorName( s );
            if ( result != null )
                return result;
        }
        // TODO michab -- check code below for simplification.
        final Color color;

        if (s.startsWith("#")) {
            switch (s.length()) {
                // RGB/hex color
                case 7:
                    color = Color.decode(s);
                    break;
                // ARGB/hex color
                case 9:
                    int alpha = Integer.decode(s.substring(0, 3));
                    int rgb = Integer.decode("#" + s.substring(3));
                    color = new Color(alpha << 24 | rgb, true);
                    break;
                default:
                    throw new Exception("invalid #RRGGBB or #AARRGGBB color string: " + s);
            }
        } else {
            String[] parts = s.split(",");
            if (parts.length < 3 || parts.length > 4) {
                throw new Exception("invalid R, G, B[, A] color string: " + s);
            }
            try {
                // with alpha component
                if (parts.length == 4) {
                    int r = Integer.parseInt(parts[0].trim());
                    int g = Integer.parseInt(parts[1].trim());
                    int b = Integer.parseInt(parts[2].trim());
                    int a = Integer.parseInt(parts[3].trim());
                    color = new Color(r, g, b, a);
                } else {
                    int r = Integer.parseInt(parts[0].trim());
                    int g = Integer.parseInt(parts[1].trim());
                    int b = Integer.parseInt(parts[2].trim());
                    color = new Color(r, g, b);
                }
            } catch (NumberFormatException e) {
                throw new Exception("invalid R, G, B[, A] color string: " + s, e);
            }
        }
        return color;
    }

    private Color checkPlainColorName( String name )
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

