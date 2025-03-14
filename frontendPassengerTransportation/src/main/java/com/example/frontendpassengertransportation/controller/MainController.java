package com.example.frontendpassengertransportation.controller;

import com.example.frontendpassengertransportation.model.Route;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Класс MainController отвечает за управление главным окном приложения
public class MainController {

    // Элементы управления из FXML
    @FXML
    private ComboBox<String> transportTypeComboBox; // ComboBox для выбора типа транспорта
    @FXML
    private ComboBox<String> departureCityComboBox; // ComboBox для выбора города отправления
    @FXML
    private ComboBox<String> destinationCityComboBox; // ComboBox для выбора города назначения
    @FXML
    private Button showAllRoutesButton; // Кнопка для отображения всех маршрутов без фильтрации
    @FXML
    private DatePicker startDatePicker; // DatePicker для выбора начальной даты
    @FXML
    private DatePicker endDatePicker; // DatePicker для выбора конечной даты

    // Метод initialize вызывается автоматически при загрузке FXML
    public void initialize() {
        // Проверка доступности сервера
        if (isServerAvailable()) {
            loadTransportTypes(); // Загрузка типов транспорта из API
            loadCities(); // Загрузка городов из API
        }

        // Обработчики для ComboBox
        transportTypeComboBox.setOnShowing(event -> {
            if (!isServerAvailable()) {
                showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
                // Закрытие ComboBox после ошибки
                Platform.runLater(() -> transportTypeComboBox.hide());
            }
        });

        departureCityComboBox.setOnShowing(event -> {
            if (!isServerAvailable()) {
                showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
                Platform.runLater(() -> departureCityComboBox.hide());
            }
        });

        destinationCityComboBox.setOnShowing(event -> {
            if (!isServerAvailable()) {
                showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
                Platform.runLater(() -> destinationCityComboBox.hide());
            }
        });

        // Обработчики для DatePicker
        startDatePicker.setOnShowing(event -> {
            if (!isServerAvailable()) {
                showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
                Platform.runLater(() -> startDatePicker.hide());
            }
        });

        endDatePicker.setOnShowing(event -> {
            if (!isServerAvailable()) {
                showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
                Platform.runLater(() -> endDatePicker.hide());
            }
        });
    }

    // Метод для проверки доступности сервера
    private boolean isServerAvailable() {
        try {
            URL url = new URL("http://localhost:8080/routes");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // Установка таймаута подключения
            connection.setConnectTimeout(5000);
            connection.connect();
            // Проверка успешного ответа от сервера
            return connection.getResponseCode() == 200;
        } catch (IOException e) {
            // Если сервер недоступен
            return false;
        }
    }

    // Метод для загрузки типов транспорта из API в ComboBox
    private void loadTransportTypes() {
        try {
            // Выполнение запроса к API
            URL url = new URL("http://localhost:8080/routes"); // URL для получения данных о маршрутах
            // Открытие соединения
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Установка метода запроса GET
            connection.setRequestMethod("GET");

            // Чтение ответа от сервера
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                // Добавление каждой строки ответа в StringBuilder
                content.append(inputLine);
            }

            in.close(); // Закрытие потока чтения
            connection.disconnect(); // Закрытие соединения

            // Преобразование JSON в список объектов Route
            Gson gson = new Gson(); // Создание объекта Gson для работы с JSON
            List<Route> routes = gson.fromJson(content.toString(), new TypeToken<List<Route>>() {}.getType());

            // Извлечение уникальных типов транспорта
            Set<String> transportTypes = new HashSet<>(); // Set для хранения уникальных значений
            for (Route route : routes) {
                // Добавление типа транспорта в Set
                transportTypes.add(route.getTransportType());
            }

            // Загрузка типов транспорта в ComboBox
            ObservableList<String> transportTypeList = FXCollections.observableArrayList(transportTypes);

            // Добавление "Микс" в начало списка
            transportTypeList.add(0, "Микс");

            // Установка элементов и выбор "Микс" по умолчанию
            transportTypeComboBox.setItems(transportTypeList);
            transportTypeComboBox.getSelectionModel().selectFirst();

        } catch (Exception e) {
            showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
        }
    }

    // Метод для загрузки городов отправления и назначения из API в ComboBox
    private void loadCities() {
        try {
            // Выполнение запроса к API
            URL url = new URL("http://localhost:8080/routes"); // URL для получения данных о маршрутах
            // Открытие соединения
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Установка метода запроса GET
            connection.setRequestMethod("GET");

            // Чтение ответа от сервера
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                // Добавление каждой строки ответа в StringBuilder
                content.append(inputLine);
            }

            in.close(); // Закрытие потока чтения
            connection.disconnect(); // Закрытие соединения

            // Преобразование JSON в список маршрутов (объектов Route)
            Gson gson = new Gson();
            List<Route> routes = gson.fromJson(content.toString(), new TypeToken<List<Route>>() {}.getType());

            // Извлечение уникальных городов отправления и назначения
            Set<String> departureCities = new HashSet<>(); // Set для городов отправления
            Set<String> destinationCities = new HashSet<>(); // Set для городов назначения
            for (Route route : routes) {
                departureCities.add(route.getDepartureCity()); // Добавление города отправления
                destinationCities.add(route.getDestinationCity()); // Добавление города назначения
            }

            // Загрузка городов в ComboBox
            // Преобразование Set в ObservableList
            ObservableList<String> departureCityList = FXCollections.observableArrayList(departureCities);
            ObservableList<String> destinationCityList = FXCollections.observableArrayList(destinationCities);

            // Установка списка городов отправления и назначения
            departureCityComboBox.setItems(departureCityList);
            destinationCityComboBox.setItems(destinationCityList);

        } catch (Exception e) {
            showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
        }
    }

    // Метод для обработки нажатия кнопки "Показать все маршруты"
    @FXML
    private void handleShowAllRoutesButton() {
        try {
            if (!isServerAvailable()) {
                showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
                return; // Выход из метода
            }

            // Закрытие текущего окна
            Stage currentStage = (Stage) showAllRoutesButton.getScene().getWindow();
            currentStage.close();

            // Загрузка нового окна (routes_all.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/routes_all.fxml"));
            Parent root = loader.load();

            // Создание новой сцены
            Scene scene = new Scene(root, 1040, 740);

            // Создание нового окна
            Stage newStage = new Stage();
            newStage.setTitle("Список всех маршрутов");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            showErrorAlert("Не удалось открыть окно со списком всех маршрутов.");
        }
    }

    // Метод для обработки нажатия кнопки "Показать маршруты по фильтру"
    @FXML
    private void handleShowRoutesWithFilterButton() {
        try {
            if (!isServerAvailable()) {
                showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
                return;
            }

            // Считывание всех введенных данных

            // Получение значения типа транспорта
            String transportType = transportTypeComboBox.getValue();
            // Если выбрано "Микс", игнорирование фильтра
            if ("Микс".equals(transportType)) {
                transportType = null;
            }
            String departureCity = departureCityComboBox.getValue();
            String destinationCity = destinationCityComboBox.getValue();
            String startDateText = startDatePicker.getEditor().getText();
            String endDateText = endDatePicker.getEditor().getText();

            LocalDate startDate = null;
            LocalDate endDate = null;

            // Проверка на совпадение города отправления и города назначения
            if (departureCity != null && destinationCity != null && departureCity.equals(destinationCity)) {
                showErrorAlert("Город отправления и город назначения не могут совпадать.");
                return;
            }

            // Проверка формата и преобразование начальной даты
            if (!startDateText.isEmpty()) {
                if (!isValidDateFormat(startDateText)) {
                    showErrorAlert("Некорректный формат начальной даты. Введите дату в формате дд.мм.гггг.");
                    return;
                }
                startDate = parseDate(startDateText);
                if (startDate == null) {
                    showErrorAlert("Некорректная начальная дата. Проверьте введенные данные.");
                    return;
                }
            }

            // Проверка формата и преобразование конечной даты
            if (!endDateText.isEmpty()) {
                if (!isValidDateFormat(endDateText)) {
                    showErrorAlert("Некорректный формат конечной даты. Введите дату в формате дд.мм.гггг.");
                    return;
                }
                endDate = parseDate(endDateText);
                if (endDate == null) {
                    showErrorAlert("Некорректная конечная дата. Проверьте введенные данные.");
                    return;
                }
            }

            // Проверка, что хотя бы одно поле заполнено
            if (transportType == null && departureCity == null && destinationCity == null && startDate == null && endDate == null) {
                showErrorAlert("Для выполнения поиска маршрутов по фильтру необходимо заполнить хотя бы одно поле.");
                return;
            }

            // Проверка, что если введена только конечная дата, то начальная дата должна быть тоже введена
            if (startDate == null && endDate != null) {
                showErrorAlert("Если вы вводите конечную дату, необходимо также ввести начальную дату.");
                return;
            }

            // Проверка, что начальная дата не позже конечной
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                showErrorAlert("Дата начала не может быть позже даты конца.");
                return;
            }

            // Проверка на корректность дат (не раньше текущей)
            if (startDate != null && startDate.isBefore(LocalDate.now())) {
                showErrorAlert("Дата начала не может быть раньше текущей.");
                return;
            }

            if (endDate != null && endDate.isBefore(LocalDate.now())) {
                showErrorAlert("Дата конца не может быть раньше текущей.");
                return;
            }

            // Закрытие текущего окна
            Stage currentStage = (Stage) showAllRoutesButton.getScene().getWindow();
            currentStage.close();

            // Загрузка нового окна
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/routes_with_filter.fxml"));
            Parent root = loader.load();

            // Получение контроллера и передача данных
            RoutesWithFilterController controller = loader.getController();
            controller.setFilterData(transportType, departureCity, destinationCity, startDate, endDate);

            // Создание новой сцены
            Scene scene = new Scene(root, 1040, 740);

            // Создание нового окна
            Stage newStage = new Stage();
            newStage.setTitle("Список маршрутов по указанному фильтру");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            showErrorAlert("Не удалось открыть окно с маршрутами по фильтру.");
        }
    }

    // Метод для проверки формата даты
    private boolean isValidDateFormat(String dateText) {
        // Регулярное выражение для проверки формата дд.мм.гггг
        String regex = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.\\d{4}$";
        return dateText.matches(regex);
    }

    // Метод для преобразования строки даты в объект LocalDate
    private LocalDate parseDate(String dateText) {
        try {
            // Разделение строки на день, месяц и год
            String[] parts = dateText.split("\\.");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            // Создание объекта LocalDate
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            return null; // Если парсинг не удался, возвращается null
        }
    }

    // Метод для обработки нажатия кнопки "Посмотреть бронирования конкретного пассажира"
    @FXML
    private void handleShowPassengerBookingsButton() {
        try {
            if (!isServerAvailable()) {
                showErrorAlert("Сервер недоступен. Пожалуйста, попробуйте позже.");
                return;
            }

            // Загрузка FXML для модального окна
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/ticket_data_entry.fxml"));
            Parent root = loader.load();

            // Создание новой сцены
            Scene scene = new Scene(root, 500, 300);

            // Создание нового окна
            Stage modalStage = new Stage();
            modalStage.setTitle("Ввод данных пользователя");
            modalStage.setScene(scene);

            // Установка модальности окна
            modalStage.initModality(Modality.APPLICATION_MODAL);
            // Установка владельца
            modalStage.initOwner(showAllRoutesButton.getScene().getWindow());

            // Показ модального окна
            modalStage.showAndWait();
        } catch (IOException e) {
            showErrorAlert("Не удалось открыть окно ввода данных пользователя.");
        }
    }

    //  Метод для отображения ошибки с помощью Alert
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Показ окна и ожидание, пока пользователь его закроет
        alert.showAndWait();
    }
}