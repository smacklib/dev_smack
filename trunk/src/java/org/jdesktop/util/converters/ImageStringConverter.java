package org.jdesktop.util.converters;

import java.awt.Image;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

public class ImageStringConverter extends ResourceConverter {

    public ImageStringConverter() {
        super(Image.class);
    }

    @Override
    public Object parseString(String s, ResourceMap resourceMap) throws ResourceConverterException {
        return ConverterUtils.loadImageIcon(s, resourceMap).getImage();
    }
}

