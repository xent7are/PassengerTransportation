package com.example.backendpassengertransportation.service;

import com.example.backendpassengertransportation.model.BookingTicket;
import com.example.backendpassengertransportation.model.Route;
import com.example.backendpassengertransportation.repository.BookingTicketRepository;
import com.example.backendpassengertransportation.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class BookingTicketService {

    @Autowired
    private BookingTicketRepository bookingTicketRepository; // Репозиторий для работы с бронированиями

    @Autowired
    private RouteRepository routeRepository; // Репозиторий для работы с маршрутами

    // Регулярное выражение для проверки формата телефона (+7 XXX XXX-XX-XX)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+7 \\d{3} \\d{3}-\\d{2}-\\d{2}$");

    // Регулярное выражение для проверки формата электронной почты
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Список допустимых доменов для электронной почты
    private static final String[] ALLOWED_DOMAINS = {"mail.ru", "inbox.ru", "yandex.ru", "gmail.com"};

    // Получение всех бронирований
    public List<BookingTicket> getAllBookingTickets() {
        return bookingTicketRepository.findAll();
    }

    // Получение бронирования по его ID
    public BookingTicket getBookingTicketById(Long idBooking) {
        return bookingTicketRepository.findById(idBooking).orElse(null);
    }

    // Создание нового бронирования
    public BookingTicket createBookingTicket(Long routeId, String passengerFullName, String passengerPhone,
                                             String passengerEmail) {
        // Проверка формата телефона
        if (!isValidPhoneFormat(passengerPhone)) {
            throw new IllegalArgumentException("Неверный формат телефона. Используйте формат: +7 XXX XXX-XX-XX");
        }

        // Проверка формата электронной почты
        if (!isValidEmailFormat(passengerEmail)) {
            throw new IllegalArgumentException("Неверный формат электронной почты. " +
                            "Используйте формат: имя_пользователя@домен. " +
                            "Имя пользователя может содержать английские буквы, цифры, точки (.), подчеркивания (_) и дефисы (-). " +
                            "Допустимые домены: mail.ru, inbox.ru, yandex.ru, gmail.com.");
        }

        // Поиск маршрута по ID
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new NoSuchElementException("Маршрут с ID " + routeId + " не найден."));

        // Проверка наличия доступных мест
        if (route.getNumberAvailableSeats() <= 0) {
            throw new IllegalStateException("Нет доступных мест для бронирования.");
        }

        // Уменьшение количества доступных мест
        route.setNumberAvailableSeats(route.getNumberAvailableSeats() - 1);
        routeRepository.save(route); // Сохраняем изменения в маршруте

        // Установка текущей даты и времени
        LocalDateTime bookingDate = LocalDateTime.now();

        // Форматирование даты и времени в строку
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = bookingDate.format(formatter);

        // Создание нового бронирования
        BookingTicket bookingTicket = new BookingTicket(route, passengerFullName, passengerPhone, passengerEmail, formattedDate);

        // Сохранение бронирования
        return bookingTicketRepository.save(bookingTicket);
    }

    // Метод для проверки формата телефона
    private boolean isValidPhoneFormat(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    // Метод для проверки формата электронной почты и допустимых доменов
    private boolean isValidEmailFormat(String email) {
        // Проверка формата email
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }

        // Извлечение домена из email
        String domain = email.substring(email.lastIndexOf("@") + 1);

        // Проверка, что домен находится в списке допустимых
        for (String allowedDomain : ALLOWED_DOMAINS) {
            if (domain.equalsIgnoreCase(allowedDomain)) {
                return true;
            }
        }

        return false;
    }

    // Удаление бронирования по ID
    public void deleteBookingTicket(Long idBooking) {
        BookingTicket bookingTicket = bookingTicketRepository.findById(idBooking).orElse(null);
        if (bookingTicket == null) {
            throw new IllegalArgumentException("Бронирование с таким ID не найдено.");
        }

        // Увеличиваем количество доступных мест
        Route route = bookingTicket.getRoute();
        route.setNumberAvailableSeats(route.getNumberAvailableSeats() + 1);
        routeRepository.save(route); // Сохраняем изменения в маршруте

        // Удаляем бронирование
        bookingTicketRepository.deleteById(idBooking);
    }


    // Получение списка бронирований для конкретного пассажира по ФИО
    public List<BookingTicket> getBookingTicketsByPassengerFullName(String passengerFullName) {
        return bookingTicketRepository.findByPassengerFullName(passengerFullName);
    }

    // Поиск бронирования по маршруту и телефону пассажира
    public BookingTicket getBookingTicketByRouteAndPassengerPhone(Long routeId, String passengerPhone) {
        // Проверка формата телефона
        if (!isValidPhoneFormat(passengerPhone)) {
            throw new IllegalArgumentException("Неверный формат телефона. Используйте формат: +7 XXX XXX-XX-XX");
        }

        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут с таким ID не найден.");
        }

        Optional<BookingTicket> bookingTicket = bookingTicketRepository.findByRouteAndPassengerPhone(route, passengerPhone);
        if (bookingTicket.isEmpty()) {
            throw new NoSuchElementException("Бронирование с номером телефона '" + passengerPhone + "' не найдено.");
        }

        return bookingTicket.get();
    }

    // Получение всех бронирований для маршрута
    public List<BookingTicket> getBookingTicketsByRoute(Long routeId) {
        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут с таким ID не найден.");
        }
        return bookingTicketRepository.findByRoute(route);
    }
}
