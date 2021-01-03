package org.smack.util.converters;

import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;

abstract class NumberStringConverter extends ResourceConverter {

    NumberStringConverter(Class<?> type, Class<?> primitiveType) {
        super(type);
    }

    protected abstract Number parseString(String s) throws NumberFormatException;

    @Override
    public Object parseString(String s, ResourceMap ignore) throws Exception {
        try {
            return parseString(s);
        } catch (NumberFormatException e) {
            throw new Exception(
                    String.format( "invalid %s: %s",
                            getType().getSimpleName(),
                            s),
                    e );
        }
    }
}
