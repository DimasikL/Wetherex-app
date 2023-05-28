package com.example.wetherapp.model

data class WeatherModel(
    val city: String,
    val time: String,
    val condition: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val ImageUrl: String,
    val hours: String,
    val country: String,
    val vWind: String,
    val pressure: String,
    val cloud: String,
    val dirWind: String,
    val sunrise: String,
    val sunset: String,
    val moonPhase: String,
    val humidity: String
)
