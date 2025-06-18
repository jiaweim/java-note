/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2024 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mjw.hansolo;

import eu.hansolo.fx.charts.WaffleChart;
import eu.hansolo.fx.charts.WaffleChartBuilder;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;


public class WaffleChartTest extends Application {
    private static final Random         RND = new Random();
    private              HBox           hBox;
    private              VBox           vBox;
    private              DoubleProperty value;
    private              Label          percentageLabel;
    private              Label          restLabel;
    private WaffleChart chart;
    private              long           lastTimerCall;
    private              AnimationTimer timer;


    @Override public void init() {
        value = new SimpleDoubleProperty(0);
        value.addListener((o, ov, nv) -> {
            chart.setValue(nv.doubleValue());
            restLabel.setText(String.format("%.0f%%", 100.0 - (nv.doubleValue() * 100.0)));
            percentageLabel.setText(String.format("%.0f%%", nv.doubleValue() * 100.0));
        });

        chart = WaffleChartBuilder.create()
                                  .cellFill(Color.PURPLE)
                                  .backgroundFill(Color.TRANSPARENT)
                                  .build();

        restLabel = new Label(String.format("%.0f%%", 0.0));
        restLabel.setTextFill(Color.BLACK);
        restLabel.setFont(Font.font(14));
        StackPane restPane = new StackPane(restLabel);
        restPane.setBackground(new Background(new BackgroundFill(chart.getEmptyCellFill(), new CornerRadii(64), new Insets(0))));
        //restPane.setBorder(new Border(new BorderStroke(Color.PURPLE,BorderStrokeStyle.SOLID, new CornerRadii(64), new BorderWidths(2))));

        restPane.setMinSize(64, 64);
        restPane.setMaxSize(64, 64);
        restPane.setPrefSize(64, 64);

        percentageLabel = new Label(String.format("%.0f%%", 0.0));
        percentageLabel.setTextFill(Color.WHITE);
        percentageLabel.setFont(Font.font(14));
        StackPane percentagePane = new StackPane(percentageLabel);
        percentagePane.setBackground(new Background(new BackgroundFill(Color.PURPLE, new CornerRadii(64), new Insets(0))));
        percentagePane.setMinSize(64, 64);
        percentagePane.setMaxSize(64, 64);
        percentagePane.setPrefSize(64, 64);

        hBox = new HBox(80, percentagePane, restPane);
        hBox.setAlignment(Pos.CENTER);

        vBox = new VBox(10, hBox, chart);
        vBox.setAlignment(Pos.CENTER);

        lastTimerCall   = System.nanoTime();
        timer           = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 3_000_000_000l) {
                    value.set(RND.nextDouble());
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(vBox);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("WaffleChart");
        stage.setScene(scene);
        stage.show();

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
