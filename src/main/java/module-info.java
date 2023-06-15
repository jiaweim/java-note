module java.module {
    requires javafx.graphics;
    requires javafx.controls;
    requires com.google.gson;
    requires java.desktop;
    requires org.junit.jupiter.api;
    requires org.apache.avro;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires javafx.swing;

    opens mjw.study.javafx.layout;
    opens mjw.study.javafx.canvas;
    opens mjw.study.javafx.node;
}