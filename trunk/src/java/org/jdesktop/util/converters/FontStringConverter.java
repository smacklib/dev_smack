package org.jdesktop.util.converters;

import java.awt.Font;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;


public class FontStringConverter extends ResourceConverter {

    public FontStringConverter() {
        super(Font.class);
    }
    /* Just delegates to Font.decode.
     * Typical string is: face-STYLE-size, for example "Arial-PLAIN-12"
     */

    @Override
    public Object parseString(String s, ResourceMap ignore) throws Exception {
        return Font.decode(s);
    }
}
