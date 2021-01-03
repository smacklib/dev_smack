package org.smack.util.converters;

import java.awt.Dimension;
import java.util.List;

import org.smack.util.resource.ResourceConverter;
import org.smack.util.resource.ResourceMap;


public class DimensionStringConverter extends ResourceConverter {

    public DimensionStringConverter() {
        super(Dimension.class);
    }

    @Override
    public Object parseString(String s, ResourceMap ignore) throws Exception {
        List<Double> xy = ConverterUtils.parseDoubles(s, 2, "invalid x,y Dimension string");
        Dimension d = new Dimension();
        d.setSize(xy.get(0), xy.get(1));
        return d;
    }
}

