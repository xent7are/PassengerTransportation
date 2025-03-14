module com.example.frontendpassengertransportation {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;

    opens com.example.frontendpassengertransportation to javafx.graphics, javafx.fxml, com.google.gson;
    opens com.example.frontendpassengertransportation.model to com.google.gson, javafx.fxml, javafx.graphics;
    opens com.example.frontendpassengertransportation.application to com.google.gson, javafx.fxml, javafx.graphics;
    opens com.example.frontendpassengertransportation.controller to com.google.gson, javafx.fxml, javafx.graphics;

    exports com.example.frontendpassengertransportation.model;
    exports com.example.frontendpassengertransportation.controller;
}