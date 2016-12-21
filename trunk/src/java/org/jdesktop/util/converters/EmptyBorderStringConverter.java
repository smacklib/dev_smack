package org.jdesktop.util.converters;

import java.util.List;

import javax.swing.border.EmptyBorder;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

public class EmptyBorderStringConverter extends ResourceConverter {

    public EmptyBorderStringConverter() {
        super(EmptyBorder.class);
    }

    @Override
    public Object parseString(String s, ResourceMap ignore) throws ResourceConverterException {
        List<Double> tlbr = ConverterUtils.parseDoubles(s, 4, "invalid top,left,bottom,right EmptyBorder string");
        return new EmptyBorder(tlbr.get(0).intValue(), tlbr.get(1).intValue(), tlbr.get(2).intValue(), tlbr.get(3).intValue());
    }
}
