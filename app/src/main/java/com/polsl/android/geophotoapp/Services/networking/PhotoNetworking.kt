package com.polsl.android.geophotoapp.Services.networking

import android.content.Context
import com.polsl.android.geophotoapp.model.PhotoFilter
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

interface UploadPhotoNetworkingDelegate {
    fun success()
    fun error(error: Throwable)
}

interface FetchPhotoNetworkingDelegate {
    fun acquired(photosId: List<Long>)
    fun error(error: Throwable)
    fun acquiredFilteredPhotos(result: List<Long>)
}

class PhotoNetworking(var context: Context) {

    var delegateUpload: UploadPhotoNetworkingDelegate? = null
    var delegateFetch: FetchPhotoNetworkingDelegate? = null

    private val apiService = GeoPhotoEndpoints.create()
    private val sharedPrefs = UserDataSharedPrefsHelper(context)

    fun uploadPhoto(photo: File) {
        val token = sharedPrefs.getAccessToken()
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), photo)
        val body = MultipartBody.Part.createFormData("photo", photo.name, reqFile)
        val upload = apiService.upoladPhoto(body, token!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        upload.subscribe({ result ->
            delegateUpload?.success()
        }, { error ->
            delegateUpload?.error(error)
        })
    }

    fun getPhotosId() {
        val token = sharedPrefs.getAccessToken()
        apiService.getPhotoIds(token!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    delegateFetch?.acquired(result)
                }, { error ->
                    delegateFetch?.error(error)
                })
    }

    fun getFilteredPhotos(photoFilter: PhotoFilter?) {
        if (photoFilter == null)
            return
        val token = sharedPrefs.getAccessToken()
        apiService.filterPhotos(token!!, photoFilter)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    delegateFetch?.acquiredFilteredPhotos(result)
                }, { error ->
                    delegateFetch?.error(error)
                })
    }

    fun getExifsForIds(photosId: List<Long>) {}
}