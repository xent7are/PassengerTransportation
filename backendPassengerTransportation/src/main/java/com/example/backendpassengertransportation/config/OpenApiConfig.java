package com.example.backendpassengertransportation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Passenger Transportation Booking System API",
                description = "API для управления системой бронирования билетов на пассажирские перевозки. Позволяет пользователям осуществлять бронирование билетов, управлять маршрутами, проверять доступность мест и получать информацию о пассажирах.",
                version = "1.0.0"
        )
)

public class OpenApiConfig {
    // Конфигурация для Swagger
}

