package org.jdesktop.util.converters;

import java.awt.Rectangle;
import java.util.List;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

public class RectangleStringConverter extends ResourceConverter {

    public RectangleStringConverter() {
        super(Rectangle.class);
    }

    @Override
    public Object parseString(String s, ResourceMap ignore) throws ResourceConverterException {
        List<Double> xywh = ConverterUtils.parseDoubles(s, 4, "invalid x,y,width,height Rectangle string");
        Rectangle r = new Rectangle();
        r.setFrame(xywh.get(0), xywh.get(1), xywh.get(2), xywh.get(3));
        return r;
    }
}
