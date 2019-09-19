package org.jdesktop.util.converters;

public class DoubleStringConverter extends NumberStringConverter {

        public DoubleStringConverter() {
            super(Double.class, double.class);
        }

        @Override
        protected Number parseString(String s) throws NumberFormatException {
            return Double.parseDouble(s);
        }
    }

