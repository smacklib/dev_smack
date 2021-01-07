package org.smack.util.converters;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;

public class ImageStringConverter extends ResourceConverter {

    public ImageStringConverter() {
        super(Image.class);
    }

    @Override
    public Object parseString(String s, ResourceMap resourceMap) throws Exception {
        return new ImageIcon( new URL( s ) ).getImage();
    }
}

