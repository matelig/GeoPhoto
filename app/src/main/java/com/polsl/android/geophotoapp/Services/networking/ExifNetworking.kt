package com.polsl.android.geophotoapp.Services.networking

import android.content.Context
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.rest.restBody.EditExifRequestBody
import com.polsl.android.geophotoapp.rest.restResponse.ExifParams
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ExifNetworkingDelegate {
    fun error(error: String?)
    fun success(params: ExifParams)
    fun uploadSuccessful()
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

    fun updateExifParams(params: ExifParams, photoId: Long) {
        var exifRequest = EditExifRequestBody(params, photoId)
        apiService.updateExifParams(exifRequest, sharedPrefs.getAccessToken()!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    delegate?.uploadSuccessful()
                }, { error ->
                    delegate?.error("Error while uploading changes")
                })
    }
}