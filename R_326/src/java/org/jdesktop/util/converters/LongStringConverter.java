package org.jdesktop.util.converters;

public class LongStringConverter extends INumberStringConverter {

    public LongStringConverter() {
        super(Long.class, long.class);
    }

    @Override
    protected Number parseString(String s, int radix) throws NumberFormatException {
        return (radix == -1) ? Long.decode(s) : Long.parseLong(s, radix);
    }
}

