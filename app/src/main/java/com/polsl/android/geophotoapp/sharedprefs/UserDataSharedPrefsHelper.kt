package com.polsl.android.geophotoapp.sharedprefs

import android.content.Context
import com.google.gson.Gson
import com.polsl.android.geophotoapp.model.UserData

/**
 * Created by alachman on 21.04.2018.
 */
class UserDataSharedPrefsHelper(private val context: Context) : BaseSharedPrefsHelper() {

    companion object {
        const val JSON_USER = "jsonUser"
    }

    fun getLoggedUser(): UserData? {
        val sharedPreferences = context.getSharedPreferences(FILE_PREFS_NAME, Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString(JSON_USER, null)
        var user: UserData? = null
        if (userJson != null) {
            user = Gson().fromJson(userJson, UserData::class.java)
        }
        return user
    }

    fun saveLoggedUser(user: UserData?): Boolean {
        val sharedPreferences = context.getSharedPreferences(FILE_PREFS_NAME, Context.MODE_PRIVATE)
        val prefsEditor = sharedPreferences.edit()
        val gson = Gson()
        if (user != null) {
            prefsEditor.putString(JSON_USER, gson.toJson(user))
        } else {
            prefsEditor.remove(JSON_USER)
        }
        return prefsEditor.commit()
    }
}