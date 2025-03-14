package com.example.frontendpassengertransportation.model;

import com.google.gson.annotations.SerializedName;

// Класс, представляющий маршрут в системе бронирования билетов
public class Route {

    // Аннотация @SerializedName используется для сопоставления поля с JSON ключом
    @SerializedName("idRoute")
    private int idRoute; // ID маршрута

    @SerializedName("transportType")
    private String transportType; // Тип транспорта

    @SerializedName("departureCity")
    private String departureCity; // Город отправления

    @SerializedName("destinationCity")
    private String destinationCity; // Город назначения

    @SerializedName("departureTime")
    private String departureTime; // Дата и время отправления

    @SerializedName("arrivalTime")
    private String arrivalTime; // Дата и время прибытия

    @SerializedName("totalNumberSeats")
    private int totalNumberSeats; // Общее количество мест в транспорте

    @SerializedName("numberAvailableSeats")
    private int numberAvailableSeats; // Количество доступных мест для бронирования

    // Метод для получения идентификатора маршрута
    public int getIdRoute() {
        return idRoute;
    }

    // Метод для установки идентификатора маршрута
    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
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

    // Метод для получения времени отправления
    public String getDepartureTime() {
        return departureTime;
    }

    // Метод для установки времени отправления
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    // Метод для получения времени прибытия
    public String getArrivalTime() {
        return arrivalTime;
    }

    // Метод для установки времени прибытия
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    // Метод для получения общего количества мест в транспорте
    public int getTotalNumberSeats() {
        return totalNumberSeats;
    }

    // Метод для установки общего количества мест в транспорте
    public void setTotalNumberSeats(int totalNumberSeats) {
        this.totalNumberSeats = totalNumberSeats;
    }

    // Метод для получения количества доступных мест для бронирования
    public int getNumberAvailableSeats() {
        return numberAvailableSeats;
    }

    // Метод для установки количества доступных мест для бронирования
    public void setNumberAvailableSeats(int numberAvailableSeats) {
        this.numberAvailableSeats = numberAvailableSeats;
    }
}