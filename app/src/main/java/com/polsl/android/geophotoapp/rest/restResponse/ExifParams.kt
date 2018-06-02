package com.polsl.android.geophotoapp.rest.restResponse

import com.google.gson.annotations.SerializedName
import java.util.*

class ExifParams(@field:SerializedName("photoId")
                 var photoId: Long? = null,
                 @field:SerializedName("photoName")
                 var photoName: String? = null,
                 @field:SerializedName("date")
                 var date: Date? = null,
                 @field:SerializedName("cameraName")
                 var cameraName: String? = null,
                 @field:SerializedName("exposure")
                 var exposure: String? = null,
                 @field:SerializedName("maxAperture")
                 var maxAperture: String? = null,
                 @field:SerializedName("focalLength")
                 var focalLength: String? = null,
                 @field:SerializedName("longitude")
                 var longitude: Double? = null,
                 @field:SerializedName("latitude")
                 var latitude: Double? = null,
                 @field:SerializedName("author")
                 var author: String? = null,
                 @field:SerializedName("description")
                 var description: String? = null)