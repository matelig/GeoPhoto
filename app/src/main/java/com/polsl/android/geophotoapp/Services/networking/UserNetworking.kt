package com.polsl.android.geophotoapp.Services.networking

import android.content.Context
import com.google.gson.Gson
import com.polsl.android.geophotoapp.model.UserData
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.rest.restResponse.LoginResponse
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface UserNetworkingDelegate {
    fun loginSuccess()
    fun registerSuccess()
    fun error(error: Throwable?)
}

class UserNetworking(var context: Context) {

    var delegate: UserNetworkingDelegate? = null

    fun login(userData: UserData) {
        UserDataSharedPrefsHelper(context).saveLoggedUser(userData)
        val requestHandle = GeoPhotoEndpoints.create()
        var call = requestHandle.login(userData)
        call.enqueue((object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                delegate?.error(t)
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                if (response != null && response.isSuccessful && response.body()?.token != null) {
                    UserDataSharedPrefsHelper(context).saveAccessToken(response?.body()?.token)
                    delegate?.loginSuccess()
                } else
                    delegate?.error(
                            Throwable(Gson().fromJson(response?.errorBody()?.string(), LoginResponse::class.java).message))
            }
        }))
    }

    fun register(userData: UserData) {
        UserDataSharedPrefsHelper(context).saveLoggedUser(userData)
        val requestHandle = GeoPhotoEndpoints.create()
        requestHandle.register(userData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    delegate?.registerSuccess()
                }, { error ->
                    delegate?.error(error)
                })
    }
}