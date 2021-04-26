package com.yourapp.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

private const val appId = "2e65127e909e178d0af311a81f39948c"
class WeatherViewModel : ViewModel() {

    lateinit var weatherModel : WeatherModel
    private val _temperature = MutableLiveData<Float>()

    val temperature : LiveData<Float>
        get() = _temperature

    private val _dummy = MutableLiveData<Float>()

    val dummy : LiveData<Float>
        get() = _dummy

    init {
        getWeatherReport("chennai")
        //_dummy.value = 2.0f
    }

     fun getWeatherReport(city : String, unit : String = "metric"){
        viewModelScope. launch{
            try{
                weatherModel = WeatherApi.service.getWeather(city, appId, unit)
                _temperature.value = weatherModel.main?.temp
                _dummy.value = 1.0f
            }
            catch (e : Exception){
                _temperature.value = 0.0f
                _dummy.value = 1.5f
            }
        }
    }
}