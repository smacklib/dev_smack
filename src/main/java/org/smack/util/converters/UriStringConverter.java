package org.smack.util.converters;

import java.net.URI;
import java.net.URISyntaxException;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

public class UriStringConverter extends ResourceConverter {

        public UriStringConverter() {
            super(URI.class);
        }

        @Override
        public Object parseString(String s, ResourceMap ignore) throws Exception {
            try {
                return new URI(s);
            } catch (URISyntaxException e) {
                throw new Exception("invalid URI: " + s, e);
            }
        }
    }
