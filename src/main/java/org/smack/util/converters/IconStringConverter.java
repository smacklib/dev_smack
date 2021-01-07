package org.smack.util.converters;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;

public class IconStringConverter extends ResourceConverter {

    public IconStringConverter() {
        super(Icon.class);
    }

    @Override
    public Object parseString(String s, ResourceMap notused) throws Exception {
        return new ImageIcon( new URL( s ) );
    }
}
