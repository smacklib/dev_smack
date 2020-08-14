package org.smack.fx;

import javafx.scene.shape.SVGPath;

/**
 *
 * @author MICBINZ
 */
public class SvgImage extends SVGPath
{
    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {

        System.out.printf( "width=%f height=%f %n", width, height );
    }

    @Override
    public double maxWidth(double height) {

        double result = super.maxWidth(height);
        System.out.println("maxw=" + result);
        return Double.MAX_VALUE;
    }
}
