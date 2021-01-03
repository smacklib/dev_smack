package org.smack.util.converters;

import java.awt.Image;

import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;

public class ImageStringConverter extends ResourceConverter {

    public ImageStringConverter() {
        super(Image.class);
    }

    @Override
    public Object parseString(String s, ResourceMap resourceMap) throws Exception {
        return ConverterUtils.loadImageIcon(s, resourceMap).getImage();
    }
}

