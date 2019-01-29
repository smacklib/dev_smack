package org.jdesktop.util.converters;


public class ByteStringConverter extends INumberStringConverter {

        public ByteStringConverter() {
            super(Byte.class, byte.class);
        }

        @Override
        public Number parseString(String s, int radix) throws NumberFormatException {
            return (radix == -1) ? Byte.decode(s) : Byte.parseByte(s, radix);
        }
    }
