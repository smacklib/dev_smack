package org.smack.util.converters;

import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;

public class BooleanStringConverter extends ResourceConverter {

    public BooleanStringConverter() {
        super(Boolean.class);
    }

    @Override
    public Object parseString(String s, ResourceMap ignore) {

        return Boolean.parseBoolean( s );
    }
}
