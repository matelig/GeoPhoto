package com.polsl.android.geophotoapp.rest

import com.polsl.android.geophotoapp.model.UserData
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface GeoPhotoEndpoints {

    @POST("/login")
    fun login(userData: UserData): Call<ResponseBody>

    @Multipart
    @POST("/uploadPhoto")
    fun upoladPhoto(@Part("photo") photo: File, @Header("Username") username: String): Call<ResponseBody>

    companion object geoPhotoApi {
        private const val URL = "https://195.181.223.56:8080"

        fun create(): GeoPhotoEndpoints {
            val builder = OkHttpClient.Builder()
            val client = builder.build()
            val retrofit = Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            return retrofit.create(GeoPhotoEndpoints::class.java)
        }
    }

}