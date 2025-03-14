package com.example.frontendpassengertransportation.controller;

import com.example.frontendpassengertransportation.model.Route;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Класс RoutesWithFilterController отвечает за управление окном со списком отфильтрованных маршрутов
public class RoutesWithFilterController {

    // Элементы интерфейса
    @FXML
    private TableView<Route> routesTable; // Таблица для отображения маршрутов
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
    private TableColumn<Route, Integer> availableSeatsColumn; // Колонка для доступных мест
    @FXML
    private TableColumn<Route, Void> bookingColumn; // Колонка для кнопки "Забронировать"

    // Параметры фильтрации
    private String transportType; // Тип транспорта
    private String departureCity; // Город отправления
    private String destinationCity; // Город назначения
    private String startDate; // Начальная дата
    private String endDate; // Конечная дата

    // Метод для форматирования даты в строку
    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);
    }

    // Метод для установки данных фильтра
    public void setFilterData(String transportType, String departureCity, String destinationCity, LocalDate startDate, LocalDate endDate) {
        this.transportType = "Микс".equals(transportType) ? null : transportType;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.startDate = startDate != null ? formatDate(startDate) : null;
        this.endDate = endDate != null ? formatDate(endDate) : null;
        loadFilteredRoutes(); // Загрузка отфильтрованных маршрутов
    }

    // Метод для загрузки маршрутов с учетом фильтров
    private void loadFilteredRoutes() {
        // Словарь для хранения маршрутов по idRoute
        Map<Integer, Route> routeMap = new HashMap<>();
        // Словарь для хранения количества совпадений с фильтрами
        Map<Integer, Integer> filterCountMap = new HashMap<>();

        try {
            // Инициализация словаря routeMap
            List<Route> allRoutes = fetchRoutes("http://localhost:8080/routes");
            for (Route route : allRoutes) {
                routeMap.put(route.getIdRoute(), route);
                filterCountMap.put(route.getIdRoute(), 0);
            }

            // Фильтр по типу транспорта
            if (transportType != null) {
                String encodedTransportType = URLEncoder.encode(transportType, StandardCharsets.UTF_8.name());
                List<Route> transportRoutes = fetchRoutes("http://localhost:8080/routes/transport/" + encodedTransportType);
                for (Route route : transportRoutes) {
                    if (routeMap.containsKey(route.getIdRoute())) {
                        filterCountMap.put(route.getIdRoute(), filterCountMap.get(route.getIdRoute()) + 1);
                    }
                }
            }

            // Фильтр по городам отправления и назначения
            if (departureCity != null && destinationCity != null) {
                String encodedDepartureCity = URLEncoder.encode(departureCity, StandardCharsets.UTF_8.name());
                String encodedDestinationCity = URLEncoder.encode(destinationCity, StandardCharsets.UTF_8.name());
                List<Route> cityRoutes = fetchRoutes("http://localhost:8080/routes/points?departurepoint=" + encodedDepartureCity + "&destinationpoint=" + encodedDestinationCity);
                for (Route route : cityRoutes) {
                    if (routeMap.containsKey(route.getIdRoute())) {
                        filterCountMap.put(route.getIdRoute(), filterCountMap.get(route.getIdRoute()) + 1);
                    }
                }
            } else if (departureCity != null) {
                String encodedDepartureCity = URLEncoder.encode(departureCity, StandardCharsets.UTF_8.name());
                List<Route> departureCityRoutes = fetchRoutes("http://localhost:8080/routes/departure/" + encodedDepartureCity);
                for (Route route : departureCityRoutes) {
                    if (routeMap.containsKey(route.getIdRoute())) {
                        filterCountMap.put(route.getIdRoute(), filterCountMap.get(route.getIdRoute()) + 1);
                    }
                }
            } else if (destinationCity != null) {
                String encodedDestinationCity = URLEncoder.encode(destinationCity, StandardCharsets.UTF_8.name());
                List<Route> destinationCityRoutes = fetchRoutes("http://localhost:8080/routes/destination/" + encodedDestinationCity);
                for (Route route : destinationCityRoutes) {
                    if (routeMap.containsKey(route.getIdRoute())) {
                        filterCountMap.put(route.getIdRoute(), filterCountMap.get(route.getIdRoute()) + 1);
                    }
                }
            }

            // Фильтр по точной дате
            if (startDate != null && endDate == null) {
                List<Route> exactDateRoutes = fetchRoutes("http://localhost:8080/routes/exactDate?exactDate=" + startDate);
                for (Route route : exactDateRoutes) {
                    if (routeMap.containsKey(route.getIdRoute())) {
                        filterCountMap.put(route.getIdRoute(), filterCountMap.get(route.getIdRoute()) + 1);
                    }
                }
            }

            // Фильтр по диапазону дат
            if (startDate != null && endDate != null) {
                List<Route> dateRangeRoutes = fetchRoutes("http://localhost:8080/routes/dateRange?startDate=" + startDate + "&endDate=" + endDate);
                for (Route route : dateRangeRoutes) {
                    if (routeMap.containsKey(route.getIdRoute())) {
                        filterCountMap.put(route.getIdRoute(), filterCountMap.get(route.getIdRoute()) + 1);
                    }
                }
            }

            // Определение количества фильтров
            int totalFilters = 0;
            if (transportType != null) {
                totalFilters++;
            }
            if (departureCity != null || destinationCity != null) {
                totalFilters++;
            }
            if (startDate != null && endDate == null) {
                totalFilters++;
            }
            if (startDate != null && endDate != null) {
                totalFilters++;
            }

            // Отбор маршрутов, которые прошли все фильтры
            List<Route> filteredRoutes = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now(); // Текущее время
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Map.Entry<Integer, Integer> entry : filterCountMap.entrySet()) {
                if (entry.getValue() == totalFilters) {
                    Route route = routeMap.get(entry.getKey());
                    LocalDateTime departureTime = LocalDateTime.parse(route.getDepartureTime(), formatter);

                    // Проверка, что до отправления осталось более 30 минут
                    if (Duration.between(now, departureTime).toMinutes() > 30) {
                        // Если условие выполняется, добавление маршрута в список отфильтрованных маршрутов
                        filteredRoutes.add(route);
                    }
                }
            }

            // Отображение отфильтрованных маршрутов
            displayRoutes(filteredRoutes);
        } catch (Exception e) {
            showErrorAlert("Произошла ошибка при загрузке маршрутов. Сервер не отвечает.");
        }
    }

    // Запрос маршрутов с сервера
    private List<Route> fetchRoutes(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Получение кода ответа сервера
            int responseCode = connection.getResponseCode();

            // Если сервер вернул код 204 (No Content) или 404 (Not Found)
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                // Возвращение пустого списка
                return Collections.emptyList();
            }

            // Если код ответа не 200 (OK), то это ошибка
            if (responseCode != HttpURLConnection.HTTP_OK) {
                showErrorAlert("Сервер вернул код ошибки: " + responseCode);
                return Collections.emptyList();
            }

            // Чтение ответа от сервера
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Преобразование JSON в список маршрутов
            Gson gson = new Gson();
            List<Route> routes = gson.fromJson(response.toString(), new TypeToken<List<Route>>() {}.getType());

            // Если сервер вернул пустой список, это не ошибка
            return routes != null ? routes : Collections.emptyList();
        } catch (IOException e) {
            // Ошибка подключения к серверу
            showErrorAlert("Не удалось подключиться к серверу. Сервер не отвечает.");
            return Collections.emptyList();
        } catch (Exception e) {
            // Другие ошибки
            showErrorAlert("Произошла непредвиденная ошибка при загрузке данных.");
            return Collections.emptyList();
        }
    }

    // Отображение маршрутов в таблице
    private void displayRoutes(List<Route> routes) {
        // Преобразование списка в ObservableList
        ObservableList<Route> observableRoutes = FXCollections.observableArrayList(routes);
        // Привязка данных к колонкам
        transportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transportType"));
        departureCityColumn.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        destinationCityColumn.setCellValueFactory(new PropertyValueFactory<>("destinationCity"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        availableSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("numberAvailableSeats"));

        // Добавление кнопки "Забронировать" в таблицу
        addBookingButtonToTable();

        // Установка данных в таблицу
        routesTable.setItems(observableRoutes);

        // Проверка, есть ли данные в списке маршрутов
        if (routes.isEmpty()) {
            // Создание Label с сообщением
            Label placeholderLabel = new Label("По данному фильтру не нашлось подходящих маршрутов");
            placeholderLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            placeholderLabel.setAlignment(Pos.CENTER); // Выравнивание по центру

            // Установка Label в качестве Placeholder
            routesTable.setPlaceholder(placeholderLabel);
        }
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
            Stage currentStage = (Stage) routesTable.getScene().getWindow();
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

    // Обработка нажатия кнопки "Назад"
    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) routesTable.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontendpassengertransportation/views/main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1040, 740);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Не удалось открыть главное окно.");
        }
    }

    //  Метод для отображения ошибки с помощью Alert
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Показ окна и ожидание, пока пользователь его закроет
    }
}