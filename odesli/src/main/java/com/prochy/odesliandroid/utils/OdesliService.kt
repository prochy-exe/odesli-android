package com.prochy.odesliandroid.utils
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface OdesliService {

    @GET("v1-alpha.1/links")
    fun getSongLink(
        @Query("url") url: String,
        @Query("userCountry") userCountry: String
    ): Call<OdesliData>// Change SongData to your data class representing the API response
}