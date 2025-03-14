package com.example.frontendpassengertransportation.controller;

import com.example.frontendpassengertransportation.model.BookingTicket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Класс TicketDataEntryController отвечает за управление окном с вводом данных пассажира для просмотра бронирований
public class TicketDataEntryController {

    // Элемент интерфейса
    @FXML
    private TextField passengerFullNameField; // Поле для ввода ФИО пассажира

    @FXML
    // Обработчик нажатия кнопки "Просмотреть бронирования"
    private void handleViewBookingsButton() {
        // Получение ФИО пассажира из текстового поля
        String passengerFullName = passengerFullNameField.getText();
        // Проверка, что поле ФИО не пустое
        if (passengerFullName.isEmpty()) {
            showErrorAlert("Поле ФИО не может быть пустым.");
            return;
        }

        try {
            // Кодировка ФИО пассажира для передачи в URL (замена пробелов на %20)
            String encodedFullName = URLEncoder.encode(passengerFullName, StandardCharsets.UTF_8.toString()).replace("+", "%20");
            // Формирование URL для запроса бронирований пассажира
            URL url = new URL("http://localhost:8080/booking-tickets/passenger/" + encodedFullName);
            // Открытие соединения с сервером
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Получение кода ответа сервера
            int responseCode = connection.getResponseCode();

            // Если сервер вернул код 200 (OK)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Чтение ответа от сервера
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                connection.disconnect();

                // Преобразование JSON-ответа в список объектов BookingTicket
                Gson gson = new Gson();
                List<BookingTicket> bookings = gson.fromJson(content.toString(), new TypeToken<List<BookingTicket>>() {}.getType());

                // Закрытие текущего окна (TicketDataEntryController)
                Stage currentStage = (Stage) passengerFullNameField.getScene().getWindow();
                currentStage.close();

                // Закрытие окна MainController (окно-владелец)
                Stage mainStage = (Stage) currentStage.getOwner();
                mainStage.close();

                // Открытие окна BookingsCurrentPassengerController для отображения бронирований
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/bookings_current_passenger.fxml"));
                Parent root = loader.load();

                // Получение контроллера нового окна и передача ему данных
                BookingsCurrentPassengerController bookingsController = loader.getController();
                bookingsController.setBookings(FXCollections.observableArrayList(bookings));
                bookingsController.setPassengerFullName(passengerFullName);

                // Создание и отображение нового окна
                Scene scene = new Scene(root, 1040, 740);
                Stage newStage = new Stage();
                newStage.setTitle("Бронирования пассажира");
                newStage.setScene(scene);
                newStage.show();
            }
            // Если сервер вернул код 404 (Not Found)
            else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                showInfoAlert("Бронирований для пассажира \"" + passengerFullName + "\" не найдено.");
            }
            // Если сервер вернул другой код ошибки
            else {
                showErrorAlert("Сервер вернул код ошибки: " + responseCode);
            }
        } catch (IOException e) {
            showErrorAlert("Не удалось подключиться к серверу.");
        }
    }

    // Метод для отображения ошибки в виде диалогового окна
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Метод для отображения информационного сообщения в виде диалогового окна
    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    // Обработчик нажатия кнопки "Назад"
    private void handleBackButton() {
        // Закрытие модального окна
        Stage stage = (Stage) passengerFullNameField.getScene().getWindow();
        stage.close();
    }
}