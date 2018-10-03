package org.jdesktop.util.converters;

import java.net.URI;
import java.net.URISyntaxException;

import org.jdesktop.application.ResourceConverter.ResourceConverterException;
import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

public class UriStringConverter extends ResourceConverter {

        public UriStringConverter() {
            super(URI.class);
        }

        @Override
        public Object parseString(String s, ResourceMap ignore) throws ResourceConverterException {
            try {
                return new URI(s);
            } catch (URISyntaxException e) {
                throw new ResourceConverterException("invalid URI", s, e);
            }
        }
    }
