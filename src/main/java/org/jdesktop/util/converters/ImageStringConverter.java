package org.jdesktop.util.converters;

import java.awt.Image;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

public class ImageStringConverter extends ResourceConverter {

    public ImageStringConverter() {
        super(Image.class);
    }

    @Override
    public Object parseString(String s, ResourceMap resourceMap) throws Exception {
        return ConverterUtils.loadImageIcon(s, resourceMap).getImage();
    }
}

