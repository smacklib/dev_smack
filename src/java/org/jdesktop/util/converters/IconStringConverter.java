package org.jdesktop.util.converters;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

public class IconStringConverter extends ResourceConverter {

    public IconStringConverter() {
        super(Icon.class);
    }

    @Override
    public Object parseString(String s, ResourceMap resourceMap) throws ResourceConverterException {
        return ConverterUtils.loadImageIcon(s, resourceMap);
    }

    @Override
    public boolean supportsType(Class<?> testType) {
        return testType.equals(Icon.class) || testType.equals(ImageIcon.class);
    }
}
