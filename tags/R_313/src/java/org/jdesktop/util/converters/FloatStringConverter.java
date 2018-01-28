package org.jdesktop.util.converters;

public class FloatStringConverter extends NumberStringConverter {

    public FloatStringConverter() {
        super(Float.class, float.class);
    }

    @Override
    protected Number parseString(String s) throws NumberFormatException {
        return Float.parseFloat(s);
    }
}
