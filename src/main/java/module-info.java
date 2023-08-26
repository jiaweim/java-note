module java.module {

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires com.google.gson;
    requires com.google.common;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    requires org.hamcrest;
    requires org.junit.jupiter.api;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    opens mjw.study.jackson.objectmapper;


    opens mjw.study.hamcrest.matcher;
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
    opens mjw.study.javafx.concurrent;
    opens mjw.study.javafx.fxml;
    opens mjw.study.javafx.shape;
    opens mjw.study.javafx.controls;
    opens mjw.study.javafx.transform;
    opens mjw.study.javafx.animation;
    opens mjw.study.javafx.image;
    opens mjw.study.javafx.effect;

}