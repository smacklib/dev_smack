package org.smack.fx;

import java.io.File;

import org.smack.util.xml.XmlUtil;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

/**
 * https://stackoverflow.com/questions/30702977/how-to-resize-button-containing-svg-image
 * @author MICBINZ
 */
public class SvgTest extends Application{

	private final int MIN_BUTTON_SIZE = 10;

    @Override
    public void start(Stage primaryStage) throws Exception {

        SVGPath svg = new SVGPath();
        {
        var content =
                XmlUtil.getXPath(
                        new File( "ic_car.xml" ), "vector/path/@android:pathData" );
        svg.setContent( content );
        }
        {
        var tint =
                XmlUtil.getXPathAs(
                        Color::web,
                        new File( "ic_car.xml" ),
                        "vector/@android:tint" );
        svg.setFill( tint );
        }

        var viewportHeight =
                XmlUtil.getXPathAs(
                        Double::parseDouble,
                        new File( "ic_car.xml" ), "vector/@android:viewportHeight" );
        var viewportWidth =
                XmlUtil.getXPathAs(
                        Double::parseDouble,
                        new File( "ic_car.xml" ), "vector/@android:viewportWidth" );

        Button buttonWithGraphics = new Button();
        buttonWithGraphics.setGraphic(svg);

        // Bind the Image scale property to the buttons size
        svg.scaleXProperty().bind(
                buttonWithGraphics.widthProperty().divide( viewportWidth ));
        svg.scaleYProperty().bind(
                buttonWithGraphics.heightProperty().divide( viewportHeight ));

        // Declare a minimum size for the button
        buttonWithGraphics.setMinSize(
                MIN_BUTTON_SIZE,
                MIN_BUTTON_SIZE);

        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(buttonWithGraphics);
        root.layoutBoundsProperty().addListener((observableValue, oldBounds, newBounds) -> {
                double size = Math.max(MIN_BUTTON_SIZE, Math.min(newBounds.getWidth(), newBounds.getHeight()));
                buttonWithGraphics.setPrefSize(size, size);
            }
        );

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	public static void main(String[] args) {
		launch(args);
	}
}
