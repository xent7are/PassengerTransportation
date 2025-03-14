package com.example.frontendpassengertransportation.controller;

import com.example.frontendpassengertransportation.model.Route;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.util.Callback;

// Класс AllRoutesController отвечает за управление окном со списком всех маршрутов
public class AllRoutesController implements Initializable {

    // Элементы интерфейса
    @FXML
    private TableView<Route> routesTable; // Таблица для отображения списка маршрутов
    @FXML
    private TableColumn<Route, String> transportTypeColumn; // Колонка для типа транспорта
    @FXML
    private TableColumn<Route, String> departureCityColumn; // Колонка для города отправления
    @FXML
    private TableColumn<Route, String> destinationCityColumn; // Колонка для города назначения
    @FXML
    private TableColumn<Route, String> departureTimeColumn; // Колонка для времени отправления
    @FXML
    private TableColumn<Route, String> arrivalTimeColumn; // Колонка для времени прибытия
    @FXML
    private TableColumn<Route, Integer> availableSeatsColumn; // Колонка для количества доступных мест
    @FXML
    private TableColumn<Route, Void> bookingColumn; // Колонка для кнопки "Забронировать"
    @FXML
    private Button backButton; // Кнопка для возврата на главное окно

    @Override
    // Метод initialize вызывается при загрузке FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Настройка связей колонок таблицы с полями модели Route
        transportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transportType"));
        departureCityColumn.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        destinationCityColumn.setCellValueFactory(new PropertyValueFactory<>("destinationCity"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        availableSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("numberAvailableSeats"));

        // Добавление кнопки "Забронировать" в каждую строку
        addBookingButtonToTable();

        // Загрузка данных о маршрутах из API
        loadRoutes();
    }

    // Метод для добавления кнопки "Забронировать" в таблицу
    private void addBookingButtonToTable() {
        // Создание ячейки для столбца "Бронирование"
        bookingColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Route, Void> call(final TableColumn<Route, Void> param) {
                return new TableCell<>() {
                    // Создание кнопки "Забронировать"
                    private final Button btn = new Button("Забронировать");
                    {
                        // Обработчик нажатия на кнопку
                        btn.setOnAction(event -> {
                            // Получение маршрута из текущей строки
                            Route route = getTableView().getItems().get(getIndex());
                            // Вызов метода для обработки бронирования
                            handleBookingButton(route);
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

    // Метод для обработки нажатия кнопки "Забронировать"
    private void handleBookingButton(Route route) {
        try {
            // Закрытие текущего окна
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

            // Загрузка окна бронирования (booking_ticket.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/booking_ticket.fxml"));
            Parent root = loader.load();

            // Получение контроллера окна бронирования
            BookingTicketController bookingController = loader.getController();
            // Передача выбранного маршрута в контроллер окна бронирования
            bookingController.setRoute(route);

            // Создание новой сцены
            Scene scene = new Scene(root, 1040, 740);

            // Создание нового окна
            Stage newStage = new Stage();
            newStage.setTitle("Бронирование билета");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            showErrorAlert("Не удалось открыть окно бронирования.");
        }
    }

    // Метод для загрузки данных о маршрутах из API
    private void loadRoutes() {
        try {
            // Выполнение запроса к API
            URL url = new URL("http://localhost:8080/routes");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Чтение ответа от сервера
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close(); // Закрытие потока чтения
            connection.disconnect(); // Закрытие соединения

            // Преобразование JSON в список объектов Route
            Gson gson = new Gson();
            List<Route> routes = gson.fromJson(content.toString(), new TypeToken<List<Route>>() {}.getType());

            // Фильтрация маршрутов
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Создание списка для хранения отфильтрованных маршрутов
            List<Route> filteredRoutes = new ArrayList<>();
            for (Route route : routes) {
                // Преобразование времени отправления маршрута из строки в объект LocalDateTime
                LocalDateTime departureTime = LocalDateTime.parse(route.getDepartureTime(), formatter);
                // Проверка условий:
                // 1) Время отправления маршрута должно быть позже текущего времени
                // 2) До отправления маршрута должно оставаться более 30 минут
                if (departureTime.isAfter(now) && Duration.between(now, departureTime).toMinutes() > 30) {
                    // Если условия выполнены, маршрут добавляется в список отфильтрованных маршрутов
                    filteredRoutes.add(route);
                }
            }

            // Загрузка данных в таблицу
            ObservableList<Route> routeList = FXCollections.observableArrayList(filteredRoutes);
            routesTable.setItems(routeList);

        } catch (IOException e) {
            showErrorAlert("Не удалось загрузить данные о маршрутах. Сервер не отвечает.");
        } catch (Exception e) {
            showErrorAlert("Произошла непредвиденная ошибка при загрузке данных.");
        }
    }

    // Обработка нажатия кнопки "Назад"
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
            newStage.setTitle("Бронирование билетов на транспорт");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            showErrorAlert("Не удалось открыть главное окно.");
        }
    }

    // Метод для отображения Alert с ошибкой
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Установка размера Alert
        alert.getDialogPane().setPrefSize(400, 180);
        alert.showAndWait();
    }
}