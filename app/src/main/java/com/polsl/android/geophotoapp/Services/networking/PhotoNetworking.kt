package com.polsl.android.geophotoapp.Services.networking

import android.content.Context
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Credentials
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

interface PhotoNetworkingDelegate {
    fun success()
    fun error(error: Throwable)
}

class PhotoNetworking(var context: Context) {

    var delegate: PhotoNetworkingDelegate? = null
    val apiService = GeoPhotoEndpoints.create()
    val sharedPrefs = UserDataSharedPrefsHelper(context)

    fun uploadPhoto(photo: File) {
        val token = sharedPrefs.getAccessToken()
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), photo)
        val body = MultipartBody.Part.createFormData("photo", photo.name, reqFile)
        val upload = apiService.upoladPhoto(body, token!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        upload.subscribe({ result ->
            delegate?.success()
        }, { error ->
            delegate?.error(error)
        })
    }
}