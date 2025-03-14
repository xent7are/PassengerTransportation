package com.example.frontendpassengertransportation.controller;

import com.example.frontendpassengertransportation.model.BookingTicket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Класс ViewBookingDetailsController отвечает за управление окном просмотра деталей бронирования и отмены бронирования
public class ViewBookingDetailsController {

    // Элементы интерфейса
    @FXML
    private Label transportTypeLabel; // Поле для отображения типа транспорта
    @FXML
    private Label departureCityLabel; // Поле для отображения города отправления
    @FXML
    private Label destinationCityLabel; // Поле для отображения города назначения
    @FXML
    private Label departureTimeLabel; // Поле для отображения времени отправления
    @FXML
    private Label arrivalTimeLabel; // Поле для отображения времени прибытия

    private BookingTicket booking; // Хранение данных о бронировании

    // Метод для установки данных о бронировании
    public void setBooking(BookingTicket booking) {
        this.booking = booking;
        // Заполнение полей данными из бронирования
        transportTypeLabel.setText(booking.getRoute().getTransportType());
        departureCityLabel.setText(booking.getRoute().getDepartureCity());
        destinationCityLabel.setText(booking.getRoute().getDestinationCity());
        departureTimeLabel.setText(booking.getRoute().getDepartureTime());
        arrivalTimeLabel.setText(booking.getRoute().getArrivalTime());
    }

    @FXML
    // Обработчик нажатия на кнопку "Отменить бронирование"
    private void handleCancelBookingButton() {
        // Получение времени отправления маршрута
        LocalDateTime departureTime = LocalDateTime.parse(booking.getRoute().getDepartureTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime now = LocalDateTime.now();

        // Проверка, прошло ли время отправления
        if (departureTime.isBefore(now)) {
            showErrorAlert("Невозможно отменить бронирование. Этот маршрут уже отправился.");
            return;
        }

        // Проверка, осталось ли до отправления менее 30 минут
        if (Duration.between(now, departureTime).toMinutes() < 30) {
            showErrorAlert("Невозможно отменить бронирование. До отправления осталось менее 30 минут.");
            return;
        }

        // Создание диалогового окна для подтверждения отмены бронирования
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Подтверждение отмены бронирования");
        confirmationAlert.setHeaderText("Вы уверены, что хотите отменить бронирование?");
        confirmationAlert.setContentText("После подтверждения ваше бронирование будет безвозвратно отменено.");

        // Ожидание ответа пользователя
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) { // Если пользователь нажал "ОК"
                try {
                    // Вызов метода для удаления бронирования
                    deleteBooking(booking.getIdBooking());

                    // Закрытие текущего окна (view_booking_details)
                    Stage currentStage = (Stage) transportTypeLabel.getScene().getWindow();
                    currentStage.close();

                    // Закрытие окна bookings_current_passenger (окно-владелец)
                    Stage bookingsStage = (Stage) currentStage.getOwner();
                    if (bookingsStage != null) {
                        bookingsStage.close();
                    }

                    // Открытие главного окна (main)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/main.fxml"));
                    Parent root = loader.load();

                    Scene scene = new Scene(root, 1040, 740);
                    Stage mainStage = new Stage();
                    mainStage.setTitle("Бронирование билетов на транспорт");
                    mainStage.setScene(scene);
                    mainStage.show();
                } catch (IOException e) {
                    showErrorAlert("Не удалось отменить бронирование.");
                }
            }
        });
    }

    // Метод для удаления бронирования через API
    private void deleteBooking(Long id) {
        try {
            // Формирование URL для удаления бронирования
            URL url = new URL("http://localhost:8080/booking-tickets/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Установка метода DELETE
            connection.setRequestMethod("DELETE");

            // Получение кода ответа сервера
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                showInfoAlert("Бронирование успешно отменено.");
            } else {
                showErrorAlert("Не удалось отменить бронирование. Код ошибки: " + responseCode);
            }
        } catch (IOException e) {
            showErrorAlert("Не удалось подключиться к серверу для отмены бронирования.");
        }
    }

    // Метод для отображения диалогового окна с ошибкой
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Метод для отображения информационного диалогового окна
    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Обработка нажатия на кнопку "Назад"
    @FXML
    private void handleBackButton() {
        // Закрытие текущего окна
        Stage currentStage = (Stage) transportTypeLabel.getScene().getWindow();
        currentStage.close();
    }
}