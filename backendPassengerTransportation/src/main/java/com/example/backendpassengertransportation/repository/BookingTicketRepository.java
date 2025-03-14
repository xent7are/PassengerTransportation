package com.example.backendpassengertransportation.repository;

import com.example.backendpassengertransportation.model.BookingTicket;
import com.example.backendpassengertransportation.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Интерфейс BookingTicketRepository наследует функциональность от JpaRepository
public interface BookingTicketRepository extends JpaRepository<BookingTicket, Long> {

    // Проверка наличия бронирования для конкретного маршрута
    boolean existsByRoute(Route route);

    // Получение списка бронирований для конкретного пассажира по его ФИО
    List<BookingTicket> findByPassengerFullName(String passengerFullName);

    // Поиск бронирования по маршруту и телефону пассажира
    Optional<BookingTicket> findByRouteAndPassengerPhone(Route route, String passengerPhone);

    // Получение всех бронирований для определенного маршрута
    List<BookingTicket> findByRoute(Route route);
}