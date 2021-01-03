package org.smack.util.converters;

import java.awt.Font;

import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;

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
