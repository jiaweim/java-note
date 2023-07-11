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
    requires com.google.common;

    opens mjw.study.javafx.layout;
    opens mjw.study.javafx.canvas;
    opens mjw.study.javafx.node;
    opens mjw.study.javafx.css;
    opens mjw.study.javafx.color;
    opens mjw.study.javafx.event;
    opens mjw.study.javafx.bean;
    opens mjw.study.javafx.stage;
    opens mjw.study.javafx.intro;
    opens mjw.study.javafx.mvc;

}