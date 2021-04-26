package com.yourapp.weatherapp

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class NestedModel(
    var data : List<Data>?
)

data class Data(
    @Json(name = "id")
    val employeeId: String?,

    val employee: Employee?
)

data class Employee(
    val name: String?,

    val salary: Salary?,

    val age: String?
)

data class Salary(
    val eur: Int?,

    val usd: Int?
)