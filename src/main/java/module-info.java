module java.module {
    requires java.desktop;

    requires com.formdev.flatlaf;

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires com.esotericsoftware.kryo.kryo5;
    opens mjw.kryo;

    requires com.google.gson;
    requires com.google.common;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    requires org.hamcrest;
    requires org.junit.jupiter.api;

    requires org.slf4j;
    requires org.jsoup;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires java.logging;
    requires org.kordamp.ikonli.swing;
    requires org.kordamp.ikonli.fontawesome5;
    requires com.miglayout.swing;

    requires hipparchus.stat;
    requires hipparchus.core;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;
    requires org.codehaus.stax2;
    requires univocity.parsers;
    requires org.jgrapht.core;
    requires org.jgrapht.io;
    requires org.jgrapht.ext;
    requires de.siegmar.fastcsv;
    requires ch.randelshofer.fastdoubleparser;
    requires dflib;
    requires dflib.csv;
    requires dflib.junit5;
    requires org.junit.jupiter.params;
    requires java.sql;
    requires jcommander;
    requires org.checkerframework.checker.qual;
    requires io.vavr;
    requires commons.math3;
    requires hipparchus.optim;
    requires reactor.core;
    requires org.reactivestreams;
    requires reactor.test;
    requires parallel.collectors;
    requires org.controlsfx.controls;

    opens mjw.hipparchus;
    opens mjw.jackson.objectmapper;
    opens mjw.jackson.xml;
    opens mjw.jackson.annotation;
    opens mjw.java.util;
    opens mjw.java.time;
    opens mjw.gson;
    opens mjw.vavr;
    opens mjw.jcommander;

    opens mjw.jgrapht;
    opens mjw.controlsfx;
    opens mjw.guava;
    opens mjw.dflib;
    opens mjw.commons.math;
    opens mjw.hamcrest.matcher;
    opens mjw.javafx.layout;
    opens mjw.javafx.canvas;
    opens mjw.javafx.chart;
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
    opens mjw.reactor;
    opens mjw.java.concurrency;
    opens mjw.hansolo;
}