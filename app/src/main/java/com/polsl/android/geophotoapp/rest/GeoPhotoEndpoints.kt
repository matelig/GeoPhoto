package com.polsl.android.geophotoapp.rest

import com.polsl.android.geophotoapp.rest.restResponse.LoginResponse
import com.polsl.android.geophotoapp.model.UserData
import com.polsl.android.geophotoapp.rest.restBody.EditExifRequestBody
import com.polsl.android.geophotoapp.rest.restResponse.ExifParams
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface GeoPhotoEndpoints {

    @POST("login")
    fun login(@Body userData: UserData): Call<LoginResponse>

    @POST("getParams")
    fun exifParams(@Body photoId: Long, @Header("Authorization") authorization: String): Call<ExifParams>

    @POST("uploadParams")
    fun updateExifParams(@Body exifBody: EditExifRequestBody, @Header("Authorization") authorization: String): Observable<ResponseBody>

    @POST("registerUser")
    fun register(@Body userData: UserData): Observable<ResponseBody>

    @POST("allPhotos")
    fun getPhotoIds(@Header("Authorization") authorization: String): Observable<List<Long>>

    @Multipart
    @POST("uploadPhoto")
    fun upoladPhoto(@Part photo: MultipartBody.Part, @Header("Authorization") authorization: String): Observable<Long>

    companion object geoPhotoApi {
        //const val URL = "http://195.181.223.56:8080/SIM/"
        val URL = "http://192.168.1.2:8080/"

        fun create(): GeoPhotoEndpoints {
            val builder = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
            val client = builder.build()
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl(URL)
                    .build()
            return retrofit.create(GeoPhotoEndpoints::class.java)
        }
    }

}