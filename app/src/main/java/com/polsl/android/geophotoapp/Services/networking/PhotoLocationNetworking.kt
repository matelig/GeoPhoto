package com.polsl.android.geophotoapp.Services.networking

import android.content.Context
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.rest.restResponse.ExifParams
import com.polsl.android.geophotoapp.rest.restResponse.PhotoLocation
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface PhotoLocationNetworkingDelegate {
    fun success(list: List<PhotoLocation>)
    fun error(error: String?)
}

class PhotoLocationNetworking(var context: Context) {

    var delegate: PhotoLocationNetworkingDelegate? = null
    private val apiService = GeoPhotoEndpoints.create()
    private val sharedPrefs = UserDataSharedPrefsHelper(context)

    fun fetchPhotoWithLocation() {
        var call = apiService.photoLocation(sharedPrefs.getAccessToken()!!)
        call.enqueue((object : Callback<List<PhotoLocation>> {
            override fun onFailure(call: Call<List<PhotoLocation>>?, t: Throwable?) {
                delegate?.error(t?.message)
            }

            override fun onResponse(call: Call<List<PhotoLocation>>?, response: Response<List<PhotoLocation>>?) {
                response?.body()?.let {
                    delegate?.success(it)
                } ?:
                delegate?.error("No response")
            }

        }))
    }
}