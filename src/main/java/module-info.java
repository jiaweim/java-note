module java.module {

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires com.google.gson;
    requires com.google.common;
    requires org.apache.beam.sdk;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    requires org.hamcrest;
    requires org.junit.jupiter.api;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;

    requires org.slf4j;
    requires org.jsoup;

    opens mjw.jackson.objectmapper;
    opens mjw.jackson.xml;
    opens mjw.jackson.annotation;
    opens mjw.java.util;

    opens mjw.hamcrest.matcher;
    opens mjw.javafx.layout;
    opens mjw.javafx.canvas;
    opens mjw.javafx.node;
    opens mjw.javafx.css;
    opens mjw.javafx.color;
    opens mjw.javafx.event;
    opens mjw.javafx.bean;
    opens mjw.javafx.stage;
    opens mjw.javafx.intro;
    opens mjw.javafx.mvc;
    opens mjw.javafx.concurrent;
    opens mjw.javafx.fxml;
    opens mjw.javafx.shape;
    opens mjw.javafx.controls;
    opens mjw.javafx.transform;
    opens mjw.javafx.animation;
    opens mjw.javafx.image;
    opens mjw.javafx.effect;

}