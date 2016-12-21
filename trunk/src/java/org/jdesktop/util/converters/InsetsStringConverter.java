package org.jdesktop.util.converters;

import java.awt.Insets;
import java.util.List;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

public class InsetsStringConverter extends ResourceConverter {

    public InsetsStringConverter() {
        super(Insets.class);
    }

    @Override
    public Object parseString(String s, ResourceMap ignore) throws ResourceConverterException {
        List<Double> tlbr = ConverterUtils.parseDoubles(s, 4, "invalid top,left,bottom,right Insets string");
        return new Insets(tlbr.get(0).intValue(), tlbr.get(1).intValue(), tlbr.get(2).intValue(), tlbr.get(3).intValue());
    }
}

