package com.example.backendpassengertransportation.controller;

import com.example.backendpassengertransportation.model.Route;
import com.example.backendpassengertransportation.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    // Сервис для работы с маршрутами
    private RouteService routeService;

    // Получение всех маршрутов
    @Operation(
            summary = "Получение всех маршрутов",
            description = "Возвращает список всех доступных маршрутов. " +
                    "Если маршруты отсутствуют, возвращает статус 404.")
    @GetMapping("")
    public ResponseEntity<?> getAllRoutes() {
        try {
            List<Route> routes = routeService.getAllRoutes();
            return ResponseEntity.ok(routes);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрута по ID
    @Operation(
            summary = "Получение маршрута по ID",
            description = "Возвращает маршрут по ID. " +
                    "Если маршрут не найден, возвращает статус 404 с сообщением об ошибке.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getRouteById(@PathVariable Long id) {
        try {
            Route route = routeService.getRouteById(id);
            return ResponseEntity.ok(route);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрута по типу транспорта
    @Operation(
            summary = "Получение маршрута по типу транспорта",
            description = "Возвращает маршрут по указанному типу транспорта. " +
                    "Если маршруты не найдены, возвращает статус 404.")
    @GetMapping("/transport/{transporttype}")
    public ResponseEntity<?> getRoutesByTransportType(@PathVariable String transporttype) {
        try {
            List<Route> routes = routeService.getRoutesByTransportType(transporttype);
            return ResponseEntity.ok(routes);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрута по пункту отправления и назначения
    @Operation(
            summary = "Получение маршрутов по пункту отправления и назначения",
            description = "Возвращает маршруты по указанному пункту отправления и назначения. " +
                    "Если маршруты не найдены, возвращает статус 404.")
    @GetMapping("/points")
    public ResponseEntity<?> getRoutesByDepartureAndDestinationPoint(
            @RequestParam String departurepoint,
            @RequestParam String destinationpoint) {
        try {
            List<Route> routes = routeService.getRoutesByDepartureAndDestinationPoint(departurepoint, destinationpoint);
            return ResponseEntity.ok(routes);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрутов по пункту отправления
    @Operation(
            summary = "Получение маршрутов по пункту отправления",
            description = "Возвращает маршруты по указанному пункту отправления. " +
                    "Если маршруты не найдены, возвращает статус 404.")
    @GetMapping("/departure/{departureCity}")
    public ResponseEntity<?> getRoutesByDepartureCity(@PathVariable String departureCity) {
        try {
            List<Route> routes = routeService.getRoutesByDepartureCity(departureCity);
            return ResponseEntity.ok(routes);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрутов по пункту назначения
    @Operation(
            summary = "Получение маршрутов по пункту назначения",
            description = "Возвращает маршруты по указанному пункту назначения. " +
                    "Если маршруты не найдены, возвращает статус 404.")
    @GetMapping("/destination/{destinationCity}")
    public ResponseEntity<?> getRoutesByDestinationCity(@PathVariable String destinationCity) {
        try {
            List<Route> routes = routeService.getRoutesByDestinationCity(destinationCity);
            return ResponseEntity.ok(routes);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Создание нового маршрута
    @Operation(
            summary = "Создание нового маршрута",
            description = "Позволяет создать новый маршрут с указанными параметрами. " +
                    "Возвращает статус 201 при успешном создании.")
    @PostMapping("")
    public ResponseEntity<?> createRoute(
            @RequestParam String transportType,
            @RequestParam String departureCity,
            @RequestParam String destinationCity,
            @RequestParam String departureTime,
            @RequestParam String arrivalTime,
            @RequestParam int totalNumberSeats,
            @RequestParam int numberAvailableSeats) {
        try {
            Route newRoute = routeService.createRoute(transportType, departureCity, destinationCity,
                    departureTime, arrivalTime, totalNumberSeats,
                    numberAvailableSeats);
            return ResponseEntity.status(HttpStatus.CREATED).body(newRoute);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Удаление маршрута по ID
    @Operation(
            summary = "Удаление маршрута по ID",
            description = "Удаляет маршрут по ID. " +
                    "Если маршрут не найден, возвращает статус 404.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long id) {
        try {
            routeService.deleteRoute(id);
            return ResponseEntity.ok("Маршрут успешно удален.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрутов на определенную дату отправления
    @Operation(
            summary = "Поиск маршрутов по точной дате отправления",
            description = "Возвращает маршруты, отправляющиеся в указанную дату. " +
                    "Если маршруты не найдены, возвращает статус 404.")
    @GetMapping("/exactDate")
    public ResponseEntity<?> getRoutesForExactDate(@RequestParam String exactDate) {
        try {
            List<Route> routes = routeService.fetchRoutesForExactDate(exactDate);
            return ResponseEntity.ok(routes);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Получение маршрутов по промежутку дат отправления
    @Operation(
            summary = "Поиск маршрутов по промежутку дат",
            description = "Возвращает маршруты, отправляющиеся в указанный промежуток дат. " +
                    "Если маршруты не найдены, возвращает статус 404.")
    @GetMapping("/dateRange")
    public ResponseEntity<?> getRoutesWithinDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            List<Route> routes = routeService.fetchRoutesWithinDateRange(startDate, endDate);
            return ResponseEntity.ok(routes);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}