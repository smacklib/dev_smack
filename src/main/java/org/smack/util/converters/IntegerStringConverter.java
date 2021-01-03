package org.smack.util.converters;


public class IntegerStringConverter extends INumberStringConverter {

    public IntegerStringConverter() {
        super(Integer.class, int.class);
    }

    @Override
    protected Number parseString(String s, int radix) throws NumberFormatException {
        return (radix == -1) ? Integer.decode(s) : Integer.parseInt(s, radix);
    }
}

