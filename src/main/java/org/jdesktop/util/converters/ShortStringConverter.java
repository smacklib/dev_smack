package org.jdesktop.util.converters;

public class ShortStringConverter extends INumberStringConverter {

    public ShortStringConverter() {
        super(Short.class, short.class);
    }

    @Override
    protected Number parseString(String s, int radix) throws NumberFormatException {
        return (radix == -1) ? Short.decode(s) : Short.parseShort(s, radix);
    }
}

