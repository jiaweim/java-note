/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2023 Gerrit Grunwald.
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

import eu.hansolo.fx.charts.wafermap.DieMap;
import eu.hansolo.fx.charts.wafermap.DieMapBuilder;
import eu.hansolo.fx.charts.wafermap.KLA;
import eu.hansolo.fx.charts.wafermap.KLAParser;
import eu.hansolo.fx.charts.wafermap.WaferMap;
import eu.hansolo.fx.charts.wafermap.WaferMapBuilder;
import eu.hansolo.fx.heatmap.ColorMapping;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;


public class WaferMapTest extends Application {
    private              WaferMap wafermap;
    private              DieMap   dieMap;


    @Override public void init() {
        String        filename = WaferMapTest.class.getResource("12.KLA").toString().replace("file:", "");
        Optional<KLA> klaOpt   = KLAParser.INSTANCE.parse(filename);

        //System.out.println(klaOpt.get());

        wafermap = WaferMapBuilder.create()
                                  .kla(klaOpt.get())
                                  .dieTextVisible(true)
                                  .densityColorsVisible(true)
                                  .defectsVisible(true)
                                  .heatmapVisible(true)
                                  .heatmapColorMapping(ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED)
                                  .heatmapSpotRadius(7)
                                  .heatmapOpacity(0.75)
                                  .waferFill(Color.LIGHTGRAY)
                                  .waferStroke(Color.BLACK)
                                  .dieTextFill(Color.BLACK)
                                  .build();

        dieMap = DieMapBuilder.create()
                              .dieTextFill(Color.LIGHTGRAY)
                              .dieTextVisible(true)
                              .densityColorsVisible(false)
                              .build();

        wafermap.selectedDieProperty().addListener(o -> Platform.runLater(() -> dieMap.setDie(wafermap.getSelectedDie())));
    }

    @Override public void start(Stage stage) {
        HBox pane  = new HBox(20, wafermap, dieMap);
        pane.setPadding(new Insets(10));
        Scene scene = new Scene(pane, Color.DARKGRAY);

        stage.setTitle("Wafermap");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override public void stop() {
        wafermap.dispose();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
