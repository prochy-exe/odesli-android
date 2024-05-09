package com.prochy.odesliandroid.utils
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers


interface LocationService {

    @Headers("Accept: application/json")
    @GET("/")
    fun getCountry(): Call<LocationData>
}