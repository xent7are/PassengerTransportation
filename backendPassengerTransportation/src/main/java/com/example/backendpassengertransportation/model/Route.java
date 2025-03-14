package com.example.backendpassengertransportation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_route")
    private Long idRoute; // ID маршрута

    @Column(name = "transport_type", nullable = false)
    private String transportType; // Тип транспорта

    @Column(name = "departure_city", nullable = false)
    private String departureCity; // Город отправления

    @Column(name = "destination_city", nullable = false)
    private String destinationCity; // Город назначения

    @Column(name = "departure_time", nullable = false)
    private String departureTime; // Дата и время отправления

    @Column(name = "arrival_time", nullable = false)
    private String arrivalTime; // Дата и время прибытия

    @Column(name = "total_number_seats", nullable = false)
    private int totalNumberSeats; // Общее количество мест

    @Column(name = "number_available_seats", nullable = false)
    private int numberAvailableSeats; // Количество доступных мест

    // Конструктор по умолчанию
    public Route() {
        // Пустой конструктор, необходим для работы с JPA
    }

    // Конструктор с параметрами для инициализации нового маршрута
    public Route(String transportType, String departureCity, String destinationCity, String departureTime,
                 String arrivalTime, int totalNumberSeats, int numberAvailableSeats) {
        this.transportType = transportType;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalNumberSeats = totalNumberSeats;
        this.numberAvailableSeats = numberAvailableSeats;
    }

    // Метод для получения идентификатора маршрута
    public Long getIdRoute() {
        return idRoute;
    }

    // Метод для получения типа транспорта
    public String getTransportType() {
        return transportType;
    }

    // Метод для установки типа транспорта
    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    // Метод для получения города отправления
    public String getDepartureCity() {
        return departureCity;
    }

    // Метод для установки города отправления
    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    // Метод для получения города назначения
    public String getDestinationCity() {
        return destinationCity;
    }

    // Метод для установки города назначения
    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    // Метод для получения даты и времени отправления
    public String getDepartureTime() {
        return departureTime;
    }

    // Метод для установки даты и времени отправления
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    // Метод для получения даты и времени прибытия
    public String getArrivalTime() {
        return arrivalTime;
    }

    // Метод для установки даты и времени прибытия
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    // Метод для получения общего количества мест
    public int getTotalNumberSeats() {
        return totalNumberSeats;
    }

    // Метод для установки общего количества мест
    public void setTotalNumberSeats(int totalNumberSeats) {
        this.totalNumberSeats = totalNumberSeats;
    }

    // Метод для получения количества свободных мест
    public int getNumberAvailableSeats() {
        return numberAvailableSeats;
    }

    // Метод для установки количества свободных мест
    public void setNumberAvailableSeats(int numberAvailableSeats) {
        this.numberAvailableSeats = numberAvailableSeats;
    }
}