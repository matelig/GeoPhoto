package com.polsl.android.geophotoapp.rest.restResponse

import com.google.gson.annotations.SerializedName

class LoginResponse(@field:SerializedName("accessToken")
                    var token: String, @field:SerializedName("message")
                    var message: String)
