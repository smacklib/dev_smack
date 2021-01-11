package org.smack.util.converters;

import java.util.ArrayList;
import java.util.List;

class ConverterUtils
{
    /**
     * String s is assumed to contain n number substrings separated by
     * commas.  Return a list of those integers or null if there are too
     * many, too few, or if a substring can't be parsed.  The format
     * of the numbers is specified by Double.valueOf().
     */
    public static List<Double> parseDoubles(String s, int n, String errorMsg) throws Exception {
        String[] doubleStrings = s.split(",", n + 1);
        if (doubleStrings.length != n) {
            throw new Exception(errorMsg + ": " + s);
        } else {
            List<Double> doubles = new ArrayList<>(n);
            for (String doubleString : doubleStrings) {
                try {
                    doubles.add(Double.valueOf(doubleString));
                } catch (NumberFormatException e) {
                    throw new Exception(errorMsg +": " + s, e);
                }
            }
            return doubles;
        }
    }
}
