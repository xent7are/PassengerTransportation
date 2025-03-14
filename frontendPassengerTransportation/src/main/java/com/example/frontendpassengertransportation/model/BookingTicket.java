package com.example.frontendpassengertransportation.model;

import com.google.gson.annotations.SerializedName;

// Класс, представляющий бронирование билета в системе
public class BookingTicket {

    // Аннотация @SerializedName используется для сопоставления поля с JSON ключом
    @SerializedName("idBooking")
    private Long idBooking; // ID бронирования

    @SerializedName("passengerFullName")
    private String passengerFullName; // Полное имя пассажира

    @SerializedName("passengerPhone")
    private String passengerPhone; // Номер телефона пассажира

    @SerializedName("passengerEmail")
    private String passengerEmail; // Электронная почта пассажира

    @SerializedName("bookingDate")
    private String bookingDate; // Дата и время бронирования

    @SerializedName("route")
    private Route route; // Маршрут, связанный с бронированием

    // Метод для получения идентификатора бронирования
    public Long getIdBooking() {
        return idBooking;
    }

    // Метод для установки идентификатора бронирования
    public void setIdBooking(Long idBooking) {
        this.idBooking = idBooking;
    }

    // Метод для получения полного имени пассажира
    public String getPassengerFullName() {
        return passengerFullName;
    }

    // Метод для установки полного имени пассажира
    public void setPassengerFullName(String passengerFullName) {
        this.passengerFullName = passengerFullName;
    }

    // Метод для получения номера телефона пассажира
    public String getPassengerPhone() {
        return passengerPhone;
    }

    // Метод для установки номера телефона пассажира
    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    // Метод для получения электронной почты пассажира
    public String getPassengerEmail() {
        return passengerEmail;
    }

    // Метод для установки электронной почты пассажира
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

    // Метод для получения маршрута, связанного с бронированием
    public Route getRoute() {
        return route;
    }

    // Метод для установки маршрута, связанного с бронированием
    public void setRoute(Route route) {
        this.route = route;
    }
}