package org.smack.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class SvgTest extends Application{

	private static final double REQUIRED_WIDTH = 50.0;
	private static final double REQUIRED_HEIGHT = 30.0;

	@Override
	public void start(Stage primaryStage) throws Exception {
		SvgImage svg = new SvgImage();
		svg.setContent("M 289.00,74.00 C 299.18,61.21 307.32,52.80 320.00,42.42 331.43,33.07 343.66,26.03 357.00,19.84 427.64,-12.98 509.92,2.91 564.42,58.28 583.93,78.10 595.94,99.15 605.58,125.00 607.76,130.86 611.37,144.75 612.54,151.00 613.15,154.23 613.28,160.06 615.58,162.44 617.49,164.42 624.11,165.84 627.00,166.86 634.80,169.62 639.97,172.04 647.00,176.42 673.69,193.07 692.76,221.39 695.83,253.00 700.60,302.03 676.64,345.41 630.00,364.00 621.17,367.52 608.48,370.99 599.00,371.00 599.00,371.00 106.00,371.00 106.00,371.00 96.50,370.99 87.00,368.97 78.00,366.00 36.29,352.22 6.21,312.25 6.00,268.00 5.77,219.90 34.76,179.34 81.00,165.02 96.78,160.14 107.02,161.00 123.00,161.00 124.59,150.68 130.49,137.79 136.05,129.00 150.70,105.88 173.22,88.99 200.00,82.65 213.13,79.55 219.79,79.85 233.00,80.00 247.37,80.17 264.61,85.94 277.00,93.00 279.11,86.37 284.67,79.45 289.00,74.00 Z");
		resize(svg, REQUIRED_WIDTH, REQUIRED_HEIGHT);
		Scene scene = new Scene(new StackPane(svg), 200, 200);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void resize(SVGPath svg, double width, double height) {

		double originalWidth = svg.prefWidth(-1);
		double originalHeight = svg.prefHeight(originalWidth);

		double scaleX = width / originalWidth;
		double scaleY = height / originalHeight;

		svg.setScaleX(scaleX);
		svg.setScaleY(scaleY);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
