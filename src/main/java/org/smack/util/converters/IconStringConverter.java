package org.smack.util.converters;

import javax.swing.Icon;

import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;

public class IconStringConverter extends ResourceConverter {

    public IconStringConverter() {
        super(Icon.class);
    }

    @Override
    public Object parseString(String s, ResourceMap resourceMap) throws Exception {
        return ConverterUtils.loadImageIcon(s, resourceMap);
    }
}
