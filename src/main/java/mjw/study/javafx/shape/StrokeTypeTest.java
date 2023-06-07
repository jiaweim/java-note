/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 JiaweiMao
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package mjw.study.javafx.shape;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

public class StrokeTypeTest extends Application {

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Rectangle r1 = new Rectangle(50, 50);
        Rectangle r2 = new Rectangle(50, 50);
        Rectangle r3 = new Rectangle(50, 50);
        Rectangle r4 = new Rectangle(50, 50);
        r1.setFill(Color.LIGHTGRAY);
        r2.setFill(Color.LIGHTGRAY);
        r3.setFill(Color.LIGHTGRAY);
        r4.setFill(Color.LIGHTGRAY);

        r2.setStroke(Color.BLACK);
        r3.setStroke(Color.BLACK);
        r4.setStroke(Color.BLACK);

        r2.setStrokeWidth(4);
        r3.setStrokeWidth(4);
        r4.setStrokeWidth(4);

        r2.setStrokeType(StrokeType.INSIDE);
        r4.setStrokeType(StrokeType.OUTSIDE);

        HBox root = new HBox(r1, r2, r3, r4);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Using different stroke types for shapes");
        primaryStage.show();
    }
}
