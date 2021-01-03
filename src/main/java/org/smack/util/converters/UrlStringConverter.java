package org.smack.util.converters;

import java.net.MalformedURLException;
import java.net.URL;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

public class UrlStringConverter extends ResourceConverter {

    public UrlStringConverter() {
        super(URL.class);
    }

    @Override
    public Object parseString(String s, ResourceMap ignore) throws Exception {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            throw new Exception("invalid URL: " + s, e);
        }
    }
}
