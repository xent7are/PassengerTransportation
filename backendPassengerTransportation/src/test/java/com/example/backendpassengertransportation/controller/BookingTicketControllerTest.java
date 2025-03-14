package com.example.backendpassengertransportation.controller;

import com.example.backendpassengertransportation.model.BookingTicket;
import com.example.backendpassengertransportation.service.BookingTicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingTicketControllerTest {

    @Mock
    private BookingTicketService bookingTicketService;

    @InjectMocks
    private BookingTicketController bookingTicketController;

    /**
     * Тест получения всех бронирований.
     * Проверка корректности возвращаемого списка бронирований.
     */
    @Test
    void testGetAllBookingTickets_Success() {
        // Создание тестовых данных: два бронирования с ФИО, номерами телефонов и email
        BookingTicket ticket1 = new BookingTicket(null, "Петров Иван Иванович", "+7 904 123-45-67", "ivan@mail.ru", "2025-03-14 10:00:00");
        BookingTicket ticket2 = new BookingTicket(null, "Сидорова Анна Петровна", "+7 911 456-78-90", "anna@gmail.com", "2025-03-14 11:00:00");
        List<BookingTicket> tickets = Arrays.asList(ticket1, ticket2);

        // Мокирование сервиса: при вызове метода getAllBookingTickets() возвращается тестовый список бронирований
        when(bookingTicketService.getAllBookingTickets()).thenReturn(tickets);

        // Вызов метода контроллера, который должен вернуть список бронирований
        List<BookingTicket> result = bookingTicketController.getAllBookingTickets();

        // Проверка, что размер списка должен быть равен 2 (так как было создано два бронирования)
        assertEquals(2, result.size());

        // Проверка, что поля возвращаемых бронирований совпадают с ожидаемыми значениями
        BookingTicket returnedTicket1 = result.get(0);
        assertEquals("Петров Иван Иванович", returnedTicket1.getPassengerFullName());
        assertEquals("+7 904 123-45-67", returnedTicket1.getPassengerPhone());
        assertEquals("ivan@mail.ru", returnedTicket1.getPassengerEmail());
        assertEquals("2025-03-14 10:00:00", returnedTicket1.getBookingDate());

        BookingTicket returnedTicket2 = result.get(1);
        assertEquals("Сидорова Анна Петровна", returnedTicket2.getPassengerFullName());
        assertEquals("+7 911 456-78-90", returnedTicket2.getPassengerPhone());
        assertEquals("anna@gmail.com", returnedTicket2.getPassengerEmail());
        assertEquals("2025-03-14 11:00:00", returnedTicket2.getBookingDate());
    }

    /**
     * Тест получения бронирования по ID (бронирование найдено).
     * Проверка корректности возвращаемого бронирования.
     */
    @Test
    void testGetBookingTicketById_Success() {
        // Создание тестового бронирования с ФИО, номером телефона и email
        BookingTicket ticket = new BookingTicket(null, "Смирнова Мария Ивановна", "+7 981 789-01-23", "maria@yandex.ru", "2025-03-14 12:00:00");

        // Мокирование сервиса: при вызове метода getBookingTicketById(1L) возвращается тестовое бронирование
        when(bookingTicketService.getBookingTicketById(1L)).thenReturn(ticket);

        // Вызов метода контроллера, который должен вернуть бронирование по ID
        BookingTicket result = bookingTicketController.getBookingTicketById(1L);

        // Проверка, что результат не должен быть null
        assertNotNull(result);
        // Проверка, что ФИО в возвращенном бронировании совпадает с тестовым значением
        assertEquals("Смирнова Мария Ивановна", result.getPassengerFullName());
    }

    /**
     * Тест получения бронирования по ID (бронирование не найдено).
     * Проверка возврата null при отсутствии бронирования.
     */
    @Test
    void testGetBookingTicketById_NotFound() {
        // Мокирование сервиса: при вызове метода getBookingTicketById(2L) возвращается null (бронирование не найдено)
        when(bookingTicketService.getBookingTicketById(2L)).thenReturn(null);

        // Вызов метода контроллера, который должен вернуть null, так как бронирование не найдено
        BookingTicket result = bookingTicketController.getBookingTicketById(2L);

        // Проверка, что результат должен быть null
        assertNull(result);
    }

    /**
     * Тест удаления бронирования (существующее бронирование).
     * Проверка корректности удаления и возврата статуса 200.
     */
    @Test
    void testDeleteBookingTicket_Success() {
        // Мокирование сервиса: при вызове метода deleteBookingTicket(1L) ничего не возвращается (успешное удаление)
        doNothing().when(bookingTicketService).deleteBookingTicket(1L);

        // Вызов метода контроллера, который должен удалить бронирование и вернуть статус 200
        ResponseEntity<String> response = bookingTicketController.deleteBookingTicket(1L);

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об успешном удалении
        assertEquals("Бронирование успешно удалено.", response.getBody());
    }

    /**
     * Тест удаления несуществующего бронирования.
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testDeleteBookingTicket_NotFound() {
        // Мокирование сервиса: при вызове метода deleteBookingTicket(2L) выбрасывается исключение (бронирование не найдено)
        doThrow(new IllegalArgumentException("Бронирование с таким ID не найдено.")).when(bookingTicketService).deleteBookingTicket(2L);

        // Вызов метода контроллера, который должен вернуть статус 404 и сообщение об ошибке
        ResponseEntity<String> response = bookingTicketController.deleteBookingTicket(2L);

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Бронирование с таким ID не найдено.", response.getBody());
    }

    /**
     * Тест создания бронирования с некорректным номером телефона.
     * Проверка возврата статуса 400 и сообщения об ошибке.
     */
    @Test
    void testCreateBookingTicket_InvalidPhone() {
        // Мокирование сервиса: при вызове метода createBookingTicket с некорректным номером телефона выбрасывается исключение
        when(bookingTicketService.createBookingTicket(1L, "Петров Иван Иванович", "12345", "ivan@mail.ru"))
                .thenThrow(new IllegalArgumentException("Неверный формат телефона"));

        // Вызов метода контроллера, который должен вернуть статус 400 и сообщение об ошибке
        ResponseEntity<?> response = bookingTicketController.createBookingTicket(1L, "Петров Иван Иванович", "12345", "ivan@mail.ru");

        // Проверка, что статус ответа должен быть 400 (Bad Request)
        assertEquals(400, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Неверный формат телефона", response.getBody());
    }

    /**
     * Тест создания бронирования с некорректным email.
     * Проверка возврата статуса 400 и сообщения об ошибке.
     */
    @Test
    void testCreateBookingTicket_InvalidEmail() {
        // Мокирование сервиса: при вызове метода createBookingTicket с некорректным email выбрасывается исключение
        when(bookingTicketService.createBookingTicket(1L, "Петров Иван Иванович", "+7 904 123-45-67", "ivan@unknown.com"))
                .thenThrow(new IllegalArgumentException("Неверный формат электронной почты"));

        // Вызов метода контроллера, который должен вернуть статус 400 и сообщение об ошибке
        ResponseEntity<?> response = bookingTicketController.createBookingTicket(1L, "Петров Иван Иванович", "+7 904 123-45-67", "ivan@unknown.com");

        // Проверка, что статус ответа должен быть 400 (Bad Request)
        assertEquals(400, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Неверный формат электронной почты", response.getBody());
    }

    /**
     * Тест создания бронирования с пустыми полями.
     * Проверка возврата статуса 400 и сообщения об ошибке.
     */
    @Test
    void testCreateBookingTicket_EmptyFields() {
        // Мокирование сервиса: при вызове метода createBookingTicket с пустыми полями выбрасывается исключение
        when(bookingTicketService.createBookingTicket(1L, "", "", ""))
                .thenThrow(new IllegalArgumentException("Некорректные данные"));

        // Вызов метода контроллера, который должен вернуть статус 400 и сообщение об ошибке
        ResponseEntity<?> response = bookingTicketController.createBookingTicket(1L, "", "", "");

        // Проверка, что статус ответа должен быть 400 (Bad Request)
        assertEquals(400, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Некорректные данные", response.getBody());
    }

    /**
     * Тест получения всех бронирований для пассажира по ФИО.
     * Проверка корректности возвращаемого списка бронирований.
     */
    @Test
    void testGetBookingTicketsByPassengerFullName_Success() {
        // Создание тестовых данных: два бронирования с одинаковым ФИО
        BookingTicket ticket1 = new BookingTicket(null, "Петров Иван Иванович", "+7 904 123-45-67", "ivan@mail.ru", "2025-03-14 10:00:00");
        BookingTicket ticket2 = new BookingTicket(null, "Петров Иван Иванович", "+7 911 456-78-90", "ivan@gmail.com", "2025-03-14 11:00:00");
        List<BookingTicket> tickets = Arrays.asList(ticket1, ticket2);

        // Мокирование сервиса: при вызове метода getBookingTicketsByPassengerFullName() возвращается тестовый список бронирований
        when(bookingTicketService.getBookingTicketsByPassengerFullName("Петров Иван Иванович")).thenReturn(tickets);

        // Вызов метода контроллера, который должен вернуть список бронирований для указанного ФИО
        ResponseEntity<List<BookingTicket>> response = bookingTicketController.getBookingTicketsByPassengerFullName("Петров Иван Иванович");

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что размер списка должен быть равен 2 (так как было создано два бронирования с одинаковым ФИО)
        assertEquals(2, response.getBody().size());
    }

    /**
     * Тест получения всех бронирований для пассажира по ФИО (бронирования не найдены).
     * Проверка возврата статуса 404 и пустого списка.
     */
    @Test
    void testGetBookingTicketsByPassengerFullName_NotFound() {
        // Мокирование сервиса: при вызове метода getBookingTicketsByPassengerFullName() возвращается пустой список
        when(bookingTicketService.getBookingTicketsByPassengerFullName("Иванов Иван Иванович")).thenReturn(Collections.emptyList());

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<List<BookingTicket>> response = bookingTicketController.getBookingTicketsByPassengerFullName("Иванов Иван Иванович");

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно быть пустым списком
        assertTrue(response.getBody().isEmpty());
    }

    /**
     * Тест поиска бронирования по маршруту и номеру телефона пассажира.
     * Проверка корректности возвращаемого бронирования.
     */
    @Test
    void testGetBookingTicketByRouteAndPassengerPhone_Success() {
        // Создание тестового бронирования
        BookingTicket ticket = new BookingTicket(null, "Петров Иван Иванович", "+7 904 123-45-67", "ivan@mail.ru", "2025-03-14 10:00:00");

        // Мокирование сервиса: при вызове метода getBookingTicketByRouteAndPassengerPhone() возвращается тестовое бронирование
        when(bookingTicketService.getBookingTicketByRouteAndPassengerPhone(1L, "+7 904 123-45-67")).thenReturn(ticket);

        // Вызов метода контроллера, который должен вернуть бронирование по маршруту и номеру телефона
        ResponseEntity<?> response = bookingTicketController.getBookingTicketByRouteAndPassengerPhone(1L, "+7 904 123-45-67");

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать тестовое бронирование
        assertEquals(ticket, response.getBody());
    }

    /**
     * Тест поиска бронирования по маршруту и номеру телефона пассажира (бронирование не найдено).
     * Проверка возврата статуса 404 и сообщения об ошибке.
     */
    @Test
    void testGetBookingTicketByRouteAndPassengerPhone_NotFound() {
        // Мокирование сервиса: при вызове метода getBookingTicketByRouteAndPassengerPhone() выбрасывается исключение NoSuchElementException
        when(bookingTicketService.getBookingTicketByRouteAndPassengerPhone(1L, "+7 904 123-45-67"))
                .thenThrow(new NoSuchElementException("Бронирование с номером телефона '+7 904 123-45-67' не найдено."));

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<?> response = bookingTicketController.getBookingTicketByRouteAndPassengerPhone(1L, "+7 904 123-45-67");

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно содержать сообщение об ошибке
        assertEquals("Бронирование с номером телефона '+7 904 123-45-67' не найдено.", response.getBody());
    }

    /**
     * Тест получения всех бронирований для маршрута.
     * Проверка корректности возвращаемого списка бронирований.
     */
    @Test
    void testGetBookingTicketsByRoute_Success() {
        // Создание тестовых данных: два бронирования для одного маршрута
        BookingTicket ticket1 = new BookingTicket(null, "Петров Иван Иванович", "+7 904 123-45-67", "ivan@mail.ru", "2025-03-14 10:00:00");
        BookingTicket ticket2 = new BookingTicket(null, "Сидорова Анна Петровна", "+7 911 456-78-90", "anna@gmail.com", "2025-03-14 11:00:00");
        List<BookingTicket> tickets = Arrays.asList(ticket1, ticket2);

        // Мокирование сервиса: при вызове метода getBookingTicketsByRoute() возвращается тестовый список бронирований
        when(bookingTicketService.getBookingTicketsByRoute(1L)).thenReturn(tickets);

        // Вызов метода контроллера, который должен вернуть список бронирований для указанного маршрута
        ResponseEntity<List<BookingTicket>> response = bookingTicketController.getBookingTicketsByRoute(1L);

        // Проверка, что статус ответа должен быть 200 (OK)
        assertEquals(200, response.getStatusCodeValue());
        // Проверка, что размер списка должен быть равен 2 (так как было создано два бронирования для одного маршрута)
        assertEquals(2, response.getBody().size());
    }

    /**
     * Тест получения всех бронирований для маршрута (бронирования не найдены).
     * Проверка возврата статуса 404 и пустого списка.
     */
    @Test
    void testGetBookingTicketsByRoute_NotFound() {
        // Мокирование сервиса: при вызове метода getBookingTicketsByRoute() возвращается пустой список
        when(bookingTicketService.getBookingTicketsByRoute(1L)).thenReturn(Collections.emptyList());

        // Вызов метода контроллера, который должен вернуть статус 404 (Not Found)
        ResponseEntity<List<BookingTicket>> response = bookingTicketController.getBookingTicketsByRoute(1L);

        // Проверка, что статус ответа должен быть 404 (Not Found)
        assertEquals(404, response.getStatusCodeValue());
        // Проверка, что тело ответа должно быть пустым списком
        assertTrue(response.getBody().isEmpty());
    }
}