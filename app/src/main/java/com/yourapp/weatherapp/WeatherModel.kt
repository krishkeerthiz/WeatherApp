package com.yourapp.weatherapp

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class WeatherModel (
    var coord: Coord? = null,

    var sys: Sys? = null,

    var weather: List<Weather>? ,

    var main: Main? = null,

    var wind: Wind? = null,

    var rain: Rain? = null,

    var clouds: Clouds? = null,

    var dt: Float = 0.toFloat(),

    var id: Int = 0,

    var name: String? = null,

    var cod: Float = 0.toFloat()

) : Serializable

data class Weather(

    var id: Int = 0,

    var main: String? = null,

    var description: String? = null,

    var icon: String? = null
) : Serializable

data class Clouds(

    var all: Float = 0.toFloat()
) : Serializable

data class Rain(

    var h3: Float = 0.toFloat()
) : Serializable

data class Wind(

    var speed: Float = 0.toFloat(),

    var deg: Float = 0.toFloat()
) : Serializable

data class Main(

    var temp: Float = 0.toFloat(),

    var humidity: Float = 0.toFloat(),

    var pressure: Float = 0.toFloat(),

    var temp_min: Float = 0.toFloat(),

    var temp_max: Float = 0.toFloat()
) : Serializable

data class Sys(

    var country: String? = null,

    var sunrise: Long = 0,

    var sunset: Long = 0
) : Serializable

data class Coord(

    var lon: Float = 0.toFloat(),

    var lat: Float = 0.toFloat()
    ) : Serializable
