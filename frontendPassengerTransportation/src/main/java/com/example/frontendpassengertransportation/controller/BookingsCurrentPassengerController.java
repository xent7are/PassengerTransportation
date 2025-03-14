package com.example.frontendpassengertransportation.controller;

import com.example.frontendpassengertransportation.model.BookingTicket;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// Класс BookingsCurrentPassengerController отвечает за управление окном, отображающим список бронирований конкретного пассажира
public class BookingsCurrentPassengerController implements Initializable {

    // Элементы интерфейса
    @FXML
    private TableView<BookingTicket> bookingsTable; // Таблица для отображения списка бронирований
    @FXML
    private TableColumn<BookingTicket, String> transportTypeColumn; // Колонка для типа транспорта
    @FXML
    private TableColumn<BookingTicket, String> departureCityColumn; // Колонка для города отправления
    @FXML
    private TableColumn<BookingTicket, String> destinationCityColumn; // Колонка для города назначения
    @FXML
    private TableColumn<BookingTicket, String> departureTimeColumn; // Колонка для времени отправления
    @FXML
    private TableColumn<BookingTicket, String> arrivalTimeColumn; // Колонка для времени прибытия
    @FXML
    private TableColumn<BookingTicket, Integer> availableSeatsColumn; // Колонка для количества доступных мест
    @FXML
    private TableColumn<BookingTicket, Void> showBookingColumn; // Колонка для кнопки "Посмотреть"

    private String passengerFullName; // Переменная для хранения ФИО пассажира

    @Override
    // Метод initialize вызывается при загрузке FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Настройка связей колонок таблицы с полями модели BookingTicket
        transportTypeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoute().getTransportType()));

        departureCityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoute().getDepartureCity()));

        destinationCityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoute().getDestinationCity()));

        departureTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoute().getDepartureTime()));

        arrivalTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoute().getArrivalTime()));

        availableSeatsColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRoute().getNumberAvailableSeats()).asObject());

        // Добавление кнопки "Посмотреть" в каждую строку
        addViewButtonToTable();
    }

    // Метод для установки списка бронирований в таблицу
    public void setBookings(ObservableList<BookingTicket> bookings) {
        bookingsTable.setItems(bookings);

        // Проверка, есть ли бронирования
        if (bookings.isEmpty()) {
            Label placeholderLabel = new Label("Бронирований у \"" + passengerFullName + "\" пока что нет.");
            placeholderLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            placeholderLabel.setAlignment(Pos.CENTER);
            bookingsTable.setPlaceholder(placeholderLabel);
        }
    }

    // Метод для установки ФИО пассажира
    public void setPassengerFullName(String passengerFullName) {
        this.passengerFullName = passengerFullName;
    }

    // Метод для добавления кнопки "Посмотреть" в таблицу
    private void addViewButtonToTable() {
        // Создание ячейки для столбца "Посмотреть"
        showBookingColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<BookingTicket, Void> call(final TableColumn<BookingTicket, Void> param) {
                return new TableCell<>() {
                    // Создание кнопки "Посмотреть"
                    private final Button btn = new Button("Посмотреть");
                    {
                        // Обработчик нажатия на кнопку
                        btn.setOnAction(event -> {
                            // Получение бронирования из текущей строки
                            BookingTicket booking = getTableView().getItems().get(getIndex());
                            // Вызов метода для обработки нажатия на кнопку
                            handleViewButton(booking);
                        });
                    }

                    @Override
                    // Метод для обновления ячейки
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        // Если строка пустая, скрыть кнопку
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Иначе отобразить кнопку
                            setGraphic(btn);
                        }
                    }
                };
            }
        });
    }

    // Метод для обработки нажатия кнопки "Посмотреть"
    private void handleViewButton(BookingTicket booking) {
        try {
            // Загрузка нового окна
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/view_booking_details.fxml"));
            Parent root = loader.load();

            // Получение контроллера нового окна
            ViewBookingDetailsController detailsController = loader.getController();
            // Передача данных о бронировании в контроллер нового окна
            detailsController.setBooking(booking);

            // Создание новой сцены
            Scene scene = new Scene(root, 1040, 740);

            // Создание нового окна
            Stage newStage = new Stage();
            newStage.setTitle("Детали бронирования");
            newStage.setScene(scene);

            // Установка текущего окна (bookings_current_passenger) как владельца
            newStage.initOwner(bookingsTable.getScene().getWindow());

            newStage.show();
        } catch (IOException e) {
            showErrorAlert("Не удалось открыть окно с деталями бронирования.");
        }
    }

    // Метод для отображения Alert с ошибкой
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    // Обработка нажатия кнопки "Назад"
    private void handleBackButton() {
        // Закрытие текущего окна (BookingsCurrentPassengerController)
        Stage currentStage = (Stage) bookingsTable.getScene().getWindow();
        currentStage.close();

        // Открытие окна main
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1040, 740);
            Stage mainStage = new Stage();
            mainStage.setTitle("Бронирование билетов на транспорт");
            mainStage.setScene(scene);
            mainStage.show();
        } catch (IOException e) {
            showErrorAlert("Не удалось открыть главное окно.");
        }
    }
}