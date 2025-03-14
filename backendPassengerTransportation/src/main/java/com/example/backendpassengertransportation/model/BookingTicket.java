package com.example.backendpassengertransportation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

@Entity
@Table(name = "booking_tickets")
public class BookingTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booking")
    private Long idBooking; // ID бронирования

    // Связь с маршрутом (Связь "многие к одному")
    @ManyToOne
    @JoinColumn(name = "id_route", nullable = false)
    private Route route; // Связь с маршрутом, на который сделано бронирование

    @Column(name = "passenger_full_name", nullable = false)
    private String passengerFullName; // ФИО пассажира

    @Column(name = "passenger_phone", nullable = false)
    private String passengerPhone; // Телефон пассажира

    @Column(name = "passenger_email", nullable = false)
    private String passengerEmail; // Электронная почта пассажира

    @Column(name = "booking_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String bookingDate; // Дата и время бронирования

    // Конструктор по умолчанию
    public BookingTicket() {
        // Пустой конструктор, необходим для работы с JPA
    }

    // Конструктор с параметрами для инициализации нового объекта бронирования
    public BookingTicket(Route route, String passengerFullName, String passengerPhone, String passengerEmail, String bookingDate) {
        this.route = route; // Связь бронирования с маршрутом
        this.passengerFullName = passengerFullName;
        this.passengerPhone = passengerPhone;
        this.passengerEmail = passengerEmail;
        this.bookingDate = bookingDate;
    }

    // Метод для получения идентификатора бронирования
    public Long getIdBooking() {
        return idBooking;
    }

    // Метод для установки идентификатора бронирования
    public void setIdBooking(Long idBooking) {
        this.idBooking = idBooking;
    }

    // Метод для получения маршрута, на который сделано бронирование
    public Route getRoute() {
        return route;
    }

    // Метод для установки маршрута для данного бронирования
    public void setRoute(Route route) {
        this.route = route;
    }

    // Метод для получения полного имени пассажира
    public String getPassengerFullName() {
        return passengerFullName;
    }

    // Метод для установки полного имени пассажира
    public void setPassengerFullName(String passengerFullName) {
        this.passengerFullName = passengerFullName;
    }

    // Метод для получения телефона пассажира
    public String getPassengerPhone() {
        return passengerPhone;
    }

    // Метод для установки телефона пассажира
    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    // Метод для получения email пассажира
    public String getPassengerEmail() {
        return passengerEmail;
    }

    // Метод для установки email пассажира
    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    // Метод для получения даты и времени бронирования
    public String getBookingDate() {
        return bookingDate;
    }

    // Метод для установки даты и времени бронирования
    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}