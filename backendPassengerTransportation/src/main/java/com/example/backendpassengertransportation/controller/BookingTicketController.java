package com.example.backendpassengertransportation.controller;

import com.example.backendpassengertransportation.model.BookingTicket;
import com.example.backendpassengertransportation.service.BookingTicketService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/booking-tickets")
public class BookingTicketController {

    @Autowired
    // Сервис для работы с бронированиями билетов
    private BookingTicketService bookingTicketService;

    // Получение всех бронирований
    @Operation(
            summary = "Список всех бронирований",
            description = "Возвращает все бронирования, доступные в системе")
    @GetMapping("")
    public List<BookingTicket> getAllBookingTickets() {
        return bookingTicketService.getAllBookingTickets();
    }

    // Получение бронирования по ID
    @Operation(
            summary = "Получение бронирования по ID",
            description = "Позволяет получить информацию о бронировании по уникальному ID")
    @GetMapping("/{id}")
    public BookingTicket getBookingTicketById(@PathVariable Long id) {
        return bookingTicketService.getBookingTicketById(id);
    }

    // Создание нового бронирования
    @Operation(
            summary = "Создание нового бронирования",
            description = "Позволяет создать новое бронирование для указанного маршрута и пассажира")
    @PostMapping("")
    public ResponseEntity<?> createBookingTicket(
            @RequestParam Long routeId,
            @RequestParam String passengerFullName,
            @RequestParam String passengerPhone,
            @RequestParam String passengerEmail) {
        try {
            BookingTicket bookingTicket = bookingTicketService.createBookingTicket(
                    routeId, passengerFullName, passengerPhone, passengerEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookingTicket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Удаление бронирования по ID
    @Operation(
            summary = "Удаление бронирования",
            description = "Удаляет бронирование по указанному ID, а также восстанавливает количество доступных мест на маршруте")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookingTicket(@PathVariable Long id) {
        try {
            bookingTicketService.deleteBookingTicket(id);
            return ResponseEntity.ok("Бронирование успешно удалено.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Получение всех бронирований для пассажира по ФИО
    @Operation(
            summary = "Поиск бронирований по имени пассажира",
            description = "Возвращает список всех бронирований, сделанных пассажиром с указанным полным именем")
    @GetMapping("/passenger/{passengerFullName}")
    public ResponseEntity<List<BookingTicket>> getBookingTicketsByPassengerFullName(@PathVariable String passengerFullName) {
        try {
            List<BookingTicket> bookingTickets = bookingTicketService.getBookingTicketsByPassengerFullName(passengerFullName);
            if (bookingTickets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bookingTickets);
            }
            return ResponseEntity.ok(bookingTickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // Поиск бронирования по маршруту и номеру телефона пассажира
    @Operation(
            summary = "Поиск бронирования по маршруту и номеру телефона",
            description = "Позволяет найти бронирование, связанное с определенным маршрутом и номером телефона пассажира")
    @GetMapping("/route/{routeId}/phone/{passengerPhone}")
    public ResponseEntity<?> getBookingTicketByRouteAndPassengerPhone(
            @PathVariable Long routeId, @PathVariable String passengerPhone) {
        try {
            BookingTicket bookingTicket = bookingTicketService.getBookingTicketByRouteAndPassengerPhone(routeId, passengerPhone);
            return ResponseEntity.ok(bookingTicket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при обработке запроса.");
        }
    }

    // Получение всех бронирований для маршрута
    @Operation(
            summary = "Список всех бронирований для маршрута",
            description = "Возвращает все бронирования для указанного маршрута")
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<BookingTicket>> getBookingTicketsByRoute(@PathVariable Long routeId) {
        try {
            List<BookingTicket> bookingTickets = bookingTicketService.getBookingTicketsByRoute(routeId);
            if (bookingTickets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bookingTickets);
            }
            return ResponseEntity.ok(bookingTickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
