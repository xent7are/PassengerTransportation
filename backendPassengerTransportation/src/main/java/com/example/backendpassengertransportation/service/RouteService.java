package com.example.backendpassengertransportation.service;

import com.example.backendpassengertransportation.model.Route;
import com.example.backendpassengertransportation.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    // Метод для получения всех маршрутов
    public List<Route> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();
        if (routes.isEmpty()) {
            throw new NoSuchElementException("Маршруты не найдены.");
        }
        return routes;
    }

    // Метод для получения маршрута по его ID
    public Route getRouteById(Long idRoute) {
        Route route = routeRepository.findById(idRoute).orElse(null);
        if (route == null) {
            throw new NoSuchElementException("Маршрут с ID " + idRoute + " не найден.");
        }
        return route;
    }

    // Метод для поиска маршрутов по пункту отправления
    public List<Route> getRoutesByDepartureCity(String departureCity) {
        List<Route> routes = routeRepository.findByDepartureCity(departureCity);
        if (routes.isEmpty()) {
            throw new NoSuchElementException("Маршруты с указанным пунктом отправления не найдены.");
        }
        return routes;
    }

    // Метод для поиска маршрутов по пункту назначения
    public List<Route> getRoutesByDestinationCity(String destinationCity) {
        List<Route> routes = routeRepository.findByDestinationCity(destinationCity);
        if (routes.isEmpty()) {
            throw new NoSuchElementException("Маршруты с указанным пунктом назначения не найдены.");
        }
        return routes;
    }

    // Метод для создания нового маршрута
    public Route createRoute(String transportType, String departureCity, String destinationCity,
                             String departureTime, String arrivalTime, int totalNumberSeats,
                             int numberAvailableSeats) {
        // Проверка на пустые значения
        if (transportType == null || transportType.isEmpty() ||
                departureCity == null || departureCity.isEmpty() ||
                destinationCity == null || destinationCity.isEmpty() ||
                departureTime == null || departureTime.isEmpty() ||
                arrivalTime == null || arrivalTime.isEmpty()) {
            throw new IllegalArgumentException("Все поля должны быть заполнены.");
        }

        // Проверка на корректность количества мест
        if (totalNumberSeats <= 0) {
            throw new IllegalArgumentException("Общее количество мест должно быть больше нуля.");
        }
        if (numberAvailableSeats < 0 || numberAvailableSeats > totalNumberSeats) {
            throw new IllegalArgumentException("Количество доступных мест должно быть в пределах от 0 до общего количества мест.");
        }

        // Проверка, что время прибытия не раньше времени отправления
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            LocalDateTime departureDateTime = LocalDateTime.parse(departureTime, formatter);
            LocalDateTime arrivalDateTime = LocalDateTime.parse(arrivalTime, formatter);

            if (arrivalDateTime.isBefore(departureDateTime)) {
                throw new IllegalArgumentException("Время прибытия не может быть раньше времени отправления.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат времени. Используйте формат 'yyyy-MM-dd HH:mm'.");
        }

        // Создание и сохранение нового маршрута
        Route newRoute = new Route(transportType, departureCity, destinationCity,
                departureTime, arrivalTime, totalNumberSeats, numberAvailableSeats);

        return routeRepository.save(newRoute);
    }

    // Метод для удаления маршрута по ID
    public void deleteRoute(Long idRoute) {
        Route route = routeRepository.findById(idRoute).orElse(null);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут с таким ID не найден.");
        }
        routeRepository.deleteById(idRoute);
    }

    // Метод для поиска маршрутов по типу транспорта
    public List<Route> getRoutesByTransportType(String transportType) {
        List<Route> routes = routeRepository.findByTransportType(transportType);
        if (routes.isEmpty()) {
            throw new IllegalStateException("Маршруты с указанным типом транспорта не найдены.");
        }
        return routes;
    }

    // Метод для поиска маршрутов по пунктам отправления и назначения
    public List<Route> getRoutesByDepartureAndDestinationPoint(String departureCity, String destinationCity) {
        if (departureCity.isEmpty() || destinationCity.isEmpty()) {
            throw new IllegalStateException("Введите пункты отправления и назначения!");
        }
        return routeRepository.findByDepartureCityAndDestinationCity(departureCity, destinationCity);
    }

    // Метод для поиска маршрутов, отправляющихся в указанную дату
    public List<Route> fetchRoutesForExactDate(String exactDate) {
        List<Route> resultRoutes = new ArrayList<>();
        // Формат входной даты
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        // Формат даты в маршруте
        DateTimeFormatter routeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Преобразование входной строки в LocalDate
            LocalDate searchDate = LocalDate.parse(exactDate, inputFormatter);

            // Получение всех маршрутов из репозитория
            List<Route> allRoutes = routeRepository.findAll();

            // Проход по каждому маршруту
            for (Route route : allRoutes) {
                // Получение даты отправления маршрута и преобразование в LocalDate
                LocalDate routeDate = LocalDate.parse(route.getDepartureTime().toString().substring(0, 10), routeFormatter);

                // Сравнение дат
                if (routeDate.isEqual(searchDate)) {
                    // Добавление маршрута в результат, если даты совпадают
                    resultRoutes.add(route);
                }
            }

            // Если маршруты не найдены, выброс исключения
            if (resultRoutes.isEmpty()) {
                throw new NoSuchElementException("Маршруты на указанную дату не найдены.");
            }

            return resultRoutes;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты. Используйте формат dd.MM.yyyy.");
        }
    }

    // Метод для поиска маршрутов, отправляющихся в указанный промежуток дат
    public List<Route> fetchRoutesWithinDateRange(String startDateStr, String endDateStr) {
        List<Route> resultRoutes = new ArrayList<>();
        // Формат входной даты
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        // Формат даты в маршруте
        DateTimeFormatter routeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Преобразование входных строк в LocalDate
            LocalDate startDate = LocalDate.parse(startDateStr, inputFormatter);
            LocalDate endDate = LocalDate.parse(endDateStr, inputFormatter);

            // Получение всех маршрутов из репозитория
            List<Route> allRoutes = routeRepository.findAll();

            // Проход по каждому маршруту
            for (Route route : allRoutes) {
                // Получение даты отправления маршрута и преобразование в LocalDate
                LocalDate routeDate = LocalDate.parse(route.getDepartureTime().toString().substring(0, 10), routeFormatter);

                // Проверка, попадает ли дата отправления маршрута в указанный промежуток
                if (!routeDate.isBefore(startDate) && !routeDate.isAfter(endDate)) {
                    // Добавление маршрута в результат, если он попадает в промежуток
                    resultRoutes.add(route);
                }
            }

            // Если маршруты не найдены, выброс исключения
            if (resultRoutes.isEmpty()) {
                throw new NoSuchElementException("Маршруты в указанном промежутке дат не найдены.");
            }

            return resultRoutes;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты. Используйте формат dd.MM.yyyy.");
        }
    }
}
