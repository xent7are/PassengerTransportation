package com.example.frontendpassengertransportation.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Основной класс приложения, который запускает JavaFX приложение
public class PassengerTransportationApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PassengerTransportationApplication.class.getResource("/com/example/frontendpassengertransportation/views/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1040, 740);
        stage.setTitle("Бронирование билетов на транспорт");
        stage.setScene(scene);
        stage.show();
    }

    // Основной метод, который запускает приложение
    public static void main(String[] args) {
        // Запуск JavaFX приложения
        launch();
    }
}