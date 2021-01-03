package org.smack.util.converters;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

abstract class INumberStringConverter extends ResourceConverter {

        INumberStringConverter(Class<?> type, Class<?> primitiveType) {
            super(type);
        }

        protected abstract Number parseString(String s, int radix) throws NumberFormatException;

        @Override
        public Object parseString(String s, ResourceMap ignore) throws Exception {
            try {
                String[] nar = s.split("&"); // number ampersand radix
                int radix = (nar.length == 2) ? Integer.parseInt(nar[1]) : -1;
                return parseString(nar[0], radix);
            } catch (NumberFormatException e) {
                String message = String.format( "Cannot convert '%s' to %s.",
                        s,
                        getType().getSimpleName() );
                throw new Exception( message, e );
            }
        }
    }

