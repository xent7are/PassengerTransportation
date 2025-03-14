package com.example.backendpassengertransportation.controller;

import com.example.backendpassengertransportation.model.Route;
import com.example.backendpassengertransportation.service.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

    @Mock
    private RouteService routeService;

    @InjectMocks
    private RouteController routeController;

    /**
     * Тест получения всех маршрутов.
     * Проверка корректности возвращаемого списка маршрутов.
     */
    @Test
    void testGetAllRoutes_Success() {
        // Создание тестовых данных: два маршрута
        Route route1 = new Route("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);
        Route route2 = new Route("Поезд", "Казань", "Екатеринбург", "2025-03-15 12:00", "2025-03-16 08:00", 100, 100);
        List<Route> routes = Arrays.asList(route1, route2);

        // Мокирование сервиса: при вызове метода getAllRoutes() возвращается тестовый список маршрутов
        when(routeService.getAllRoutes()).thenReturn(routes);

        // Вызов метода контроллера, который должен вернуть список маршрутов
        ResponseEntity<?> response = routeController.getAllRoutes();

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());

        // Преобразование тела ответа в список маршрутов
        List<Route> result = (List<Route>) response.getBody();

        // Проверка, что размер списка должен быть равен 2 (так как было создано два маршрута)
        assertEquals(2, result.size());

        // Проверка, что поля возвращаемых маршрутов совпадают с ожидаемыми значениями
        Route returnedRoute1 = result.get(0);
        assertEquals("Автобус", returnedRoute1.getTransportType());
        assertEquals("Москва", returnedRoute1.getDepartureCity());
        assertEquals("Санкт-Петербург", returnedRoute1.getDestinationCity());
        assertEquals("2025-03-14 10:00", returnedRoute1.getDepartureTime());
        assertEquals("2025-03-14 18:00", returnedRoute1.getArrivalTime());
        assertEquals(50, returnedRoute1.getTotalNumberSeats());
        assertEquals(50, returnedRoute1.getNumberAvailableSeats());

        Route returnedRoute2 = result.get(1);
        assertEquals("Поезд", returnedRoute2.getTransportType());
        assertEquals("Казань", returnedRoute2.getDepartureCity());
        assertEquals("Екатеринбург", returnedRoute2.getDestinationCity());
        assertEquals("2025-03-15 12:00", returnedRoute2.getDepartureTime());
        assertEquals("2025-03-16 08:00", returnedRoute2.getArrivalTime());
        assertEquals(100, returnedRoute2.getTotalNumberSeats());
        assertEquals(100, returnedRoute2.getNumberAvailableSeats());
    }

    /**
     * Тест получения всех маршрутов (маршруты не найдены).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testGetAllRoutes_NotFound() {
        // Мокирование сервиса: при вызове метода getAllRoutes() выбрасывается исключение NoSuchElementException
        when(routeService.getAllRoutes()).thenThrow(new NoSuchElementException("Маршруты не найдены."));

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = routeController.getAllRoutes();

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Маршруты не найдены.", response.getBody());
    }

    /**
     * Тест получения маршрута по ID.
     * Проверка корректности возвращаемого маршрута.
     */
    @Test
    void testGetRouteById_Success() {
        // Создание тестового маршрута
        Route route = new Route("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);

        // Мокирование сервиса: при вызове метода getRouteById(1L) возвращается тестовый маршрут
        when(routeService.getRouteById(1L)).thenReturn(route);

        // Вызов метода контроллера, который должен вернуть маршрут по ID
        ResponseEntity<?> response = routeController.getRouteById(1L);

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать тестовый маршрут
        assertEquals(route, response.getBody());
    }

    /**
     * Тест получения маршрута по ID (маршрут не найден).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testGetRouteById_NotFound() {
        // Мокирование сервиса: при вызове метода getRouteById(2L) выбрасывается исключение NoSuchElementException
        when(routeService.getRouteById(2L)).thenThrow(new NoSuchElementException("Маршрут с ID 2 не найден."));

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = routeController.getRouteById(2L);

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Маршрут с ID 2 не найден.", response.getBody());
    }

    /**
     * Тест получения маршрутов по типу транспорта.
     * Проверка корректности возвращаемого списка маршрутов.
     */
    @Test
    void testGetRoutesByTransportType_Success() {
        // Создание тестовых данных: два маршрута с типом транспорта "Автобус"
        Route route1 = new Route("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);
        Route route2 = new Route("Автобус", "Казань", "Екатеринбург", "2025-03-15 12:00", "2025-03-16 08:00", 100, 100);
        List<Route> routes = Arrays.asList(route1, route2);

        // Мокирование сервиса: при вызове метода getRoutesByTransportType("Автобус") возвращается тестовый список маршрутов
        when(routeService.getRoutesByTransportType("Автобус")).thenReturn(routes);

        // Вызов метода контроллера, который должен вернуть список маршрутов по типу транспорта
        ResponseEntity<?> response = routeController.getRoutesByTransportType("Автобус");

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что размер списка должен быть равен 2 (так как было создано два маршрута)
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    /**
     * Тест получения маршрутов по типу транспорта (маршруты не найдены).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testGetRoutesByTransportType_NotFound() {
        // Мокирование сервиса: при вызове метода getRoutesByTransportType("Самолет") выбрасывается исключение NoSuchElementException
        when(routeService.getRoutesByTransportType("Самолет")).thenThrow(new NoSuchElementException("Маршруты с указанным типом транспорта не найдены."));

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = routeController.getRoutesByTransportType("Самолет");

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Маршруты с указанным типом транспорта не найдены.", response.getBody());
    }

    /**
     * Тест получения маршрутов по пунктам отправления и назначения.
     * Проверка корректности возвращаемого списка маршрутов.
     */
    @Test
    void testGetRoutesByDepartureAndDestinationPoint_Success() {
        // Создание тестовых данных: два маршрута с пунктами отправления и назначения
        Route route1 = new Route("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);
        Route route2 = new Route("Поезд", "Москва", "Санкт-Петербург", "2025-03-15 12:00", "2025-03-16 08:00", 100, 100);
        List<Route> routes = Arrays.asList(route1, route2);

        // Мокирование сервиса: при вызове метода getRoutesByDepartureAndDestinationPoint() возвращается тестовый список маршрутов
        when(routeService.getRoutesByDepartureAndDestinationPoint("Москва", "Санкт-Петербург")).thenReturn(routes);

        // Вызов метода контроллера, который должен вернуть список маршрутов по пунктам отправления и назначения
        ResponseEntity<?> response = routeController.getRoutesByDepartureAndDestinationPoint("Москва", "Санкт-Петербург");

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что размер списка должен быть равен 2 (так как было создано два маршрута)
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    /**
     * Тест получения маршрутов по пунктам отправления и назначения (маршруты не найдены).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testGetRoutesByDepartureAndDestinationPoint_NotFound() {
        // Мокирование сервиса: при вызове метода getRoutesByDepartureAndDestinationPoint() выбрасывается исключение NoSuchElementException
        when(routeService.getRoutesByDepartureAndDestinationPoint("Казань", "Екатеринбург"))
                .thenThrow(new NoSuchElementException("Маршруты с указанными пунктами отправления и назначения не найдены."));

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = routeController.getRoutesByDepartureAndDestinationPoint("Казань", "Екатеринбург");

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Маршруты с указанными пунктами отправления и назначения не найдены.", response.getBody());
    }

    /**
     * Тест получения маршрутов по пункту отправления.
     * Проверка корректности возвращаемого списка маршрутов.
     */
    @Test
    void testGetRoutesByDepartureCity_Success() {
        // Создание тестовых данных: два маршрута с пунктом отправления "Москва"
        Route route1 = new Route("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);
        Route route2 = new Route("Поезд", "Москва", "Казань", "2025-03-15 12:00", "2025-03-16 08:00", 100, 100);
        List<Route> routes = Arrays.asList(route1, route2);

        // Мокирование сервиса: при вызове метода getRoutesByDepartureCity("Москва") возвращается тестовый список маршрутов
        when(routeService.getRoutesByDepartureCity("Москва")).thenReturn(routes);

        // Вызов метода контроллера, который должен вернуть список маршрутов по пункту отправления
        ResponseEntity<?> response = routeController.getRoutesByDepartureCity("Москва");

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что размер списка должен быть равен 2 (так как было создано два маршрута)
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    /**
     * Тест получения маршрутов по пункту отправления (маршруты не найдены).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testGetRoutesByDepartureCity_NotFound() {
        // Мокирование сервиса: при вызове метода getRoutesByDepartureCity("Казань") выбрасывается исключение NoSuchElementException
        when(routeService.getRoutesByDepartureCity("Казань")).thenThrow(new NoSuchElementException("Маршруты с указанным пунктом отправления не найдены."));

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = routeController.getRoutesByDepartureCity("Казань");

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Маршруты с указанным пунктом отправления не найдены.", response.getBody());
    }

    /**
     * Тест получения маршрутов по пункту назначения.
     * Проверка корректности возвращаемого списка маршрутов.
     */
    @Test
    void testGetRoutesByDestinationCity_Success() {
        // Создание тестовых данных: два маршрута с пунктом назначения "Санкт-Петербург"
        Route route1 = new Route("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);
        Route route2 = new Route("Поезд", "Казань", "Санкт-Петербург", "2025-03-15 12:00", "2025-03-16 08:00", 100, 100);
        List<Route> routes = Arrays.asList(route1, route2);

        // Мокирование сервиса: при вызове метода getRoutesByDestinationCity("Санкт-Петербург") возвращается тестовый список маршрутов
        when(routeService.getRoutesByDestinationCity("Санкт-Петербург")).thenReturn(routes);

        // Вызов метода контроллера, который должен вернуть список маршрутов по пункту назначения
        ResponseEntity<?> response = routeController.getRoutesByDestinationCity("Санкт-Петербург");

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что размер списка должен быть равен 2 (так как было создано два маршрута)
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    /**
     * Тест получения маршрутов по пункту назначения (маршруты не найдены).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testGetRoutesByDestinationCity_NotFound() {
        // Мокирование сервиса: при вызове метода getRoutesByDestinationCity("Екатеринбург") выбрасывается исключение NoSuchElementException
        when(routeService.getRoutesByDestinationCity("Екатеринбург")).thenThrow(new NoSuchElementException("Маршруты с указанным пунктом назначения не найдены."));

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = routeController.getRoutesByDestinationCity("Екатеринбург");

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Маршруты с указанным пунктом назначения не найдены.", response.getBody());
    }

    /**
     * Тест создания нового маршрута.
     * Проверка корректности создания и возврата статуса 201.
     */
    @Test
    void testCreateRoute_Success() {
        // Создание тестового маршрута
        Route route = new Route("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);

        // Мокирование сервиса: при вызове метода createRoute() возвращается тестовый маршрут
        when(routeService.createRoute("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50))
                .thenReturn(route);

        // Вызов метода контроллера, который должен создать маршрут и вернуть статус 201 (Created)
        ResponseEntity<?> response = routeController.createRoute("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);

        // Проверка, что статус ответа должен быть 201 (Created)
        assertEquals(201, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать тестовый маршрут
        assertEquals(route, response.getBody());
    }

    /**
     * Тест создания нового маршрута (некорректные данные).
     * Проверка возврата статуса 400 и сообщения об ошибке.
     */
    @Test
    void testCreateRoute_InvalidData() {
        // Мокирование сервиса: при вызове метода createRoute() выбрасывается исключение IllegalArgumentException
        when(routeService.createRoute("", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50))
                .thenThrow(new IllegalArgumentException("Все поля должны быть заполнены."));

        // Вызов метода контроллера, который должен вернуть статус 400 (Bad Request)
        ResponseEntity<?> response = routeController.createRoute("", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);

        // Проверка, что статус ответа должен быть 400 (Bad Request)
        assertEquals(400, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Все поля должны быть заполнены.", response.getBody());
    }

    /**
     * Тест удаления маршрута по ID.
     * Проверка корректности удаления и возврата статуса 200.
     */
    @Test
    void testDeleteRoute_Success() {
        // Мокирование сервиса: при вызове метода deleteRoute(1L) ничего не возвращается (успешное удаление)
        doNothing().when(routeService).deleteRoute(1L);

        // Вызов метода контроллера, который должен удалить маршрут и вернуть статус 200 (OK)
        ResponseEntity<?> response = routeController.deleteRoute(1L);

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об успешном удалении
        assertEquals("Маршрут успешно удален.", response.getBody());
    }

    /**
     * Тест удаления маршрута по ID (маршрут не найден).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testDeleteRoute_NotFound() {
        // Мокирование сервиса: при вызове метода deleteRoute(2L) выбрасывается исключение NoSuchElementException
        doThrow(new NoSuchElementException("Маршрут с ID 2 не найден.")).when(routeService).deleteRoute(2L);

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = routeController.deleteRoute(2L);

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Маршрут с ID 2 не найден.", response.getBody());
    }

    /**
     * Тест получения маршрутов на определенную дату отправления.
     * Проверка корректности возвращаемого списка маршрутов.
     */
    @Test
    void testGetRoutesForExactDate_Success() {
        // Создание тестовых данных: два маршрута с датой отправления "14.03.2025"
        Route route1 = new Route("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);
        Route route2 = new Route("Поезд", "Казань", "Екатеринбург", "2025-03-14 12:00", "2025-03-15 08:00", 100, 100);
        List<Route> routes = Arrays.asList(route1, route2);

        // Мокирование сервиса: при вызове метода fetchRoutesForExactDate("14.03.2025") возвращается тестовый список маршрутов
        when(routeService.fetchRoutesForExactDate("14.03.2025")).thenReturn(routes);

        // Вызов метода контроллера, который должен вернуть список маршрутов на указанную дату
        ResponseEntity<?> response = routeController.getRoutesForExactDate("14.03.2025");

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что размер списка должен быть равен 2 (так как было создано два маршрута)
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    /**
     * Тест получения маршрутов на определенную дату отправления (маршруты не найдены).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testGetRoutesForExactDate_NotFound() {
        // Мокирование сервиса: при вызове метода fetchRoutesForExactDate("15.03.2025") выбрасывается исключение NoSuchElementException
        when(routeService.fetchRoutesForExactDate("15.03.2025")).thenThrow(new NoSuchElementException("Маршруты на указанную дату не найдены."));

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = routeController.getRoutesForExactDate("15.03.2025");

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Маршруты на указанную дату не найдены.", response.getBody());
    }

    /**
     * Тест получения маршрутов на определенную дату отправления (некорректный формат даты).
     * Проверка возврата статуса 400 и сообщения об ошибке.
     */
    @Test
    void testGetRoutesForExactDate_InvalidDateFormat() {
        // Мокирование сервиса: при вызове метода fetchRoutesForExactDate("2025-03-14") выбрасывается исключение IllegalArgumentException
        when(routeService.fetchRoutesForExactDate("2025-03-14")).thenThrow(new IllegalArgumentException("Неверный формат даты. Используйте формат dd.MM.yyyy."));

        // Вызов метода контроллера, который должен вернуть статус 400 (Bad Request)
        ResponseEntity<?> response = routeController.getRoutesForExactDate("2025-03-14");

        // Проверка, что статус ответа должен быть 400 (Bad Request)
        assertEquals(400, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Неверный формат даты. Используйте формат dd.MM.yyyy.", response.getBody());
    }

    /**
     * Тест получения маршрутов по промежутку дат отправления.
     * Проверка корректности возвращаемого списка маршрутов.
     */
    @Test
    void testGetRoutesWithinDateRange_Success() {
        // Создание тестовых данных: два маршрута с датой отправления в промежутке "14.03.2025" - "15.03.2025"
        Route route1 = new Route("Автобус", "Москва", "Санкт-Петербург", "2025-03-14 10:00", "2025-03-14 18:00", 50, 50);
        Route route2 = new Route("Поезд", "Казань", "Екатеринбург", "2025-03-15 12:00", "2025-03-16 08:00", 100, 100);
        List<Route> routes = Arrays.asList(route1, route2);

        // Мокирование сервиса: при вызове метода fetchRoutesWithinDateRange("14.03.2025", "15.03.2025") возвращается тестовый список маршрутов
        when(routeService.fetchRoutesWithinDateRange("14.03.2025", "15.03.2025")).thenReturn(routes);

        // Вызов метода контроллера, который должен вернуть список маршрутов в указанном промежутке дат
        ResponseEntity<?> response = routeController.getRoutesWithinDateRange("14.03.2025", "15.03.2025");

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что размер списка должен быть равен 2 (так как было создано два маршрута)
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    /**
     * Тест получения маршрутов по промежутку дат отправления (маршруты не найдены).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testGetRoutesWithinDateRange_NotFound() {
        // Мокирование сервиса: при вызове метода fetchRoutesWithinDateRange("16.03.2025", "17.03.2025") выбрасывается исключение NoSuchElementException
        when(routeService.fetchRoutesWithinDateRange("16.03.2025", "17.03.2025"))
                .thenThrow(new NoSuchElementException("Маршруты в указанном промежутке дат не найдены."));

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = routeController.getRoutesWithinDateRange("16.03.2025", "17.03.2025");

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Маршруты в указанном промежутке дат не найдены.", response.getBody());
    }

    /**
     * Тест получения маршрутов по промежутку дат отправления (некорректный формат даты).
     * Проверка возврата статуса 400 и сообщения об ошибке.
     */
    @Test
    void testGetRoutesWithinDateRange_InvalidDateFormat() {
        // Мокирование сервиса: при вызове метода fetchRoutesWithinDateRange("2025-03-14", "2025-03-15") выбрасывается исключение IllegalArgumentException
        when(routeService.fetchRoutesWithinDateRange("2025-03-14", "2025-03-15"))
                .thenThrow(new IllegalArgumentException("Неверный формат даты. Используйте формат dd.MM.yyyy."));

        // Вызов метода контроллера, который должен вернуть статус 400 (Bad Request)
        ResponseEntity<?> response = routeController.getRoutesWithinDateRange("2025-03-14", "2025-03-15");

        // Проверка, что статус ответа должен быть 400 (Bad Request)
        assertEquals(400, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Неверный формат даты. Используйте формат dd.MM.yyyy.", response.getBody());
    }
}