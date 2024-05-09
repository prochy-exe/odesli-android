package com.prochy.odesliandroid.utils

import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    fun getData(context: Context, link: String, countryCode: String, callback: (OdesliData) -> Unit) {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://api.song.link/").addConverterFactory(
            GsonConverterFactory.create()).build()

        val service: OdesliService = retrofit.create(OdesliService::class.java)
        val call: Call<OdesliData> = service.getSongLink(link, countryCode)
        call.enqueue(object : Callback<OdesliData> {
            override fun onResponse(call: Call<OdesliData>, response: Response<OdesliData>) {
                if(response.isSuccessful){
                    val data: OdesliData = response.body() as OdesliData
                    callback(data)
                }
            }
            override fun onFailure(call: Call<OdesliData>, t: Throwable) {
                Toast.makeText(context, "Odesli request Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getCountry(context: Context, callback: (LocationData) -> Unit) {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://ipinfo.io").addConverterFactory(
            GsonConverterFactory.create()).build()
        val service: LocationService = retrofit.create(LocationService::class.java)
        val call: Call<LocationData> = service.getCountry()
        call.enqueue(object : Callback<LocationData> {
            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>) {
                if(response.isSuccessful){
                    val data: LocationData = response.body() as LocationData
                    callback(data)
                }
            }
            override fun onFailure(call: Call<LocationData>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}