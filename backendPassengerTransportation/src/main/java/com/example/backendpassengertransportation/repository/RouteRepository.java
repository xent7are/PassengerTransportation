package com.example.backendpassengertransportation.repository;

import com.example.backendpassengertransportation.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Интерфейс RouteRepository наследует функциональность от JpaRepository
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    // Поиск маршрутов по типу транспорта
    List<Route> findByTransportType(String transportType);

    // Поиск маршрутов по городу отправления и назначения
    List<Route> findByDepartureCityAndDestinationCity(String departureCity, String destinationCity);

    // Поиск маршрутов по пункту отправления
    List<Route> findByDepartureCity(String departureCity);

    // Поиск маршрутов по пункту назначения
    List<Route> findByDestinationCity(String destinationCity);
}
