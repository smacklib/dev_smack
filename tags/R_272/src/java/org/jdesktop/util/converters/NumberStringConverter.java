package org.jdesktop.util.converters;

import org.jdesktop.application.ResourceConverter.ResourceConverterException;
import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

abstract class NumberStringConverter extends ResourceConverter {

    NumberStringConverter(Class<?> type, Class<?> primitiveType) {
        super(type);
    }

    protected abstract Number parseString(String s) throws NumberFormatException;

    @Override
    public Object parseString(String s, ResourceMap ignore) throws ResourceConverterException {
        try {
            return parseString(s);
        } catch (NumberFormatException e) {
            throw new ResourceConverterException("invalid " + type.getSimpleName(), s, e);
        }
    }
}
