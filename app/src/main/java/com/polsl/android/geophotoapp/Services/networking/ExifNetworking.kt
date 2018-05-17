package com.polsl.android.geophotoapp.Services.networking

import android.content.Context
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.rest.restResponse.ExifParams
import com.polsl.android.geophotoapp.rest.restResponse.LoginResponse
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ExifNetworkingDelegate {
    fun error(error: String?)
    fun success(params: ExifParams)
}

class ExifNetworking(var context: Context) {

    var delegate: ExifNetworkingDelegate? = null
    private val apiService = GeoPhotoEndpoints.create()
    private val sharedPrefs = UserDataSharedPrefsHelper(context)

    fun getExifParameters(photoId: Long) {
        var call = apiService.exifParams(photoId, sharedPrefs.getAccessToken()!!)
        call.enqueue((object : Callback<ExifParams> {
            override fun onFailure(call: Call<ExifParams>?, t: Throwable?) {
                delegate?.error(t?.message)
            }

            override fun onResponse(call: Call<ExifParams>?, response: Response<ExifParams>?) {
                response?.body()?.let {
                    delegate?.success(it)
                } ?:
                        delegate?.error("No response")
            }

        }))
    }

    fun updateExifParams(params: ExifParams) {
        //TODO: post data to server
    }
}