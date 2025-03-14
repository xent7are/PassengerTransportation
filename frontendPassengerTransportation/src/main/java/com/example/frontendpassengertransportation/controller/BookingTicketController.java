package com.example.frontendpassengertransportation.controller;

import com.example.frontendpassengertransportation.model.Route;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// Класс BookingTicketController отвечает за управление окном бронирования билетов
public class BookingTicketController {

    // Элементы интерфейса
    @FXML
    private Label transportTypeLabel; // Label для отображения типа транспорта
    @FXML
    private Label departureCityLabel; // Label для отображения города отправления
    @FXML
    private Label destinationCityLabel; // Label для отображения города назначения
    @FXML
    private Label departureTimeLabel; // Label для отображения времени отправления
    @FXML
    private Label arrivalTimeLabel; // Label для отображения времени прибытия
    @FXML
    private TextField passengerNameField; // Поле для ввода имени пассажира
    @FXML
    private TextField passengerPhoneField; // Поле для ввода телефона пассажира
    @FXML
    private TextField passengerEmailField; // Поле для ввода email пассажира
    @FXML
    private Button bookButton; // Кнопка для бронирования
    @FXML
    private Button backButton; // Кнопка для возврата на предыдущее окно

    private Route route; // Объект маршрута, переданный из предыдущего окна
    private int idRoute; // ID маршрута для создания бронирования

    @FXML
    // Метод initialize вызывается при загрузке FXML
    public void initialize() {
        // Отключение фокуса на текстовых полях
        passengerNameField.setFocusTraversable(false);
        passengerPhoneField.setFocusTraversable(false);
        passengerEmailField.setFocusTraversable(false);
    }

    // Метод для установки маршрута и обновления интерфейса
    public void setRoute(Route route) {
        this.route = route;
        this.idRoute = route.getIdRoute(); // Сохранение ID маршрута
        updateUI(); // Обновление интерфейса с данными маршрута
    }

    // Метод для обновления интерфейса с данными маршрута
    private void updateUI() {
        if (route != null) {
            // Установка данных маршрута в соответствующие Label
            transportTypeLabel.setText(route.getTransportType());
            departureCityLabel.setText(route.getDepartureCity());
            destinationCityLabel.setText(route.getDestinationCity());
            departureTimeLabel.setText(route.getDepartureTime());
            arrivalTimeLabel.setText(route.getArrivalTime());
        }
    }

    // Метод для обработки нажатия кнопки "Забронировать"
    @FXML
    private void handleBookButton() {
        // Получение данных от пользователя
        String passengerName = passengerNameField.getText();
        String passengerPhone = passengerPhoneField.getText();
        String passengerEmail = passengerEmailField.getText();

        // Проверка, что все поля заполнены
        if (passengerName.isEmpty() || passengerPhone.isEmpty() || passengerEmail.isEmpty()) {
            showErrorAlert("Ошибка", "Все поля должны быть заполнены.");
            return;
        }

        try {
            // Кодирование параметров запроса
            String encodedPassengerName = URLEncoder.encode(passengerName, StandardCharsets.UTF_8.toString());
            String encodedPassengerPhone = URLEncoder.encode(passengerPhone, StandardCharsets.UTF_8.toString());
            String encodedPassengerEmail = URLEncoder.encode(passengerEmail, StandardCharsets.UTF_8.toString());

            // Формирование параметров запроса для создания бронирования
            String params = "routeId=" + idRoute +
                    "&passengerFullName=" + encodedPassengerName +
                    "&passengerPhone=" + encodedPassengerPhone +
                    "&passengerEmail=" + encodedPassengerEmail;

            // Отправление запроса на создание бронирования
            createBookingTicket(params);
        } catch (Exception e) {
            showErrorAlert("Ошибка", "Произошла ошибка при отправке данных на сервер.");
        }
    }

    // Метод для отправки запроса на создание бронирования
    private void createBookingTicket(String params) {
        try {
            // URL для создания бронирования
            URL url = new URL("http://localhost:8080/booking-tickets");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // Установка типа содержимого
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Разрешение отправки данных
            connection.setDoOutput(true);

            // Отправка данных на сервер
            try (OutputStream os = connection.getOutputStream()) {
                // Преобразование параметров в байты
                byte[] input = params.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Получение ответа от сервера
            int responseCode = connection.getResponseCode();
            // Если бронирование успешно создано
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                showSuccessAlert("Успех", "Бронирование успешно создано!");

                // Закрытие текущего окна
                Stage currentStage = (Stage) bookButton.getScene().getWindow();
                currentStage.close();

                // Открытие главного окна (main.fxml)
                openMainWindow();
            } else {
                // Если произошла ошибка
                // Чтение сообщения об ошибке от сервера
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        // Сборка ответа с сервера
                        response.append(responseLine.trim());
                    }
                    showErrorAlert("Ошибка", "Ошибка при создании бронирования: " + responseCode + " - " + response.toString());
                }
            }
        } catch (IOException e) {
            showErrorAlert("Ошибка", "Произошла ошибка при отправке данных на сервер.");
        } catch (Exception e) {
            showErrorAlert("Ошибка", "Произошла непредвиденная ошибка.");
        }
    }

    // Метод для открытия главного окна
    private void openMainWindow() {
        try {
            // Загрузка главного окна (main.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/main.fxml"));
            Parent root = loader.load();

            // Создание новой сцены
            Scene scene = new Scene(root, 1040, 740);

            // Создание нового окна
            Stage newStage = new Stage();
            newStage.setTitle("Бронирование билетов на транспорт");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            showErrorAlert("Ошибка", "Не удалось открыть главное окно.");
        }
    }

    // Метод для обработки нажатия кнопки "Назад"
    @FXML
    private void handleBackButton() {
        try {
            // Закрытие текущего окна
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

            // Загрузка главного окна (main.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/main.fxml"));
            Parent root = loader.load();

            // Создание новой сцены
            Scene scene = new Scene(root, 1040, 740);

            // Создание нового окна
            Stage newStage = new Stage();
            newStage.setTitle("Список всех маршрутов");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            showErrorAlert("Ошибка", "Не удалось открыть главное окно.");
        }
    }

    // Метод для отображения Alert с ошибкой
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Установка размера Alert
        alert.getDialogPane().setPrefSize(400, 180);
        alert.showAndWait();
    }

    // Метод для отображения Alert с успехом
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}