package org.jdesktop.util.converters;

import org.jdesktop.application.ResourceConverter.ResourceConverterException;
import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

abstract class NumberStringConverter extends ResourceConverter {

    private final Class<?> primitiveType;

    NumberStringConverter(Class<?> type, Class<?> primitiveType) {
        super(type);
        this.primitiveType = primitiveType;
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

    @Override
    public boolean supportsType(Class<?> testType) {
        return testType.equals(type) || testType.equals(primitiveType);
    }
}
