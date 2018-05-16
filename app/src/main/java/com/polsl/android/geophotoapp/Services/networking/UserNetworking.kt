package com.polsl.android.geophotoapp.Services.networking

import android.content.Context
import com.polsl.android.geophotoapp.model.UserData
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*

interface UserNetworkingDelegate {
    fun success()
    fun error(error: Throwable)
}

class UserNetworking(var context: Context) {

    var delegate: UserNetworkingDelegate? = null

    fun login(userData: UserData) {
        UserDataSharedPrefsHelper(context).saveLoggedUser(userData)
        val requestHandle = GeoPhotoEndpoints.create()
        requestHandle.login(userData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    delegate?.success()
                }, { error ->
                    delegate?.error(error)
                })
    }

    fun register(userData: UserData) {
        UserDataSharedPrefsHelper(context).saveLoggedUser(userData)
        val requestHandle = GeoPhotoEndpoints.create()
        requestHandle.register(userData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    delegate?.success()
                }, { error ->
                    delegate?.error(error)
                })
    }
}