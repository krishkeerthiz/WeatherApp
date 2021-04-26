package com.yourapp.weatherapp

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("/random_joke")
    suspend fun getValues() : Response<ResponseBody>
}