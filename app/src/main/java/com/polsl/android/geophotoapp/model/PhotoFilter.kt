package com.polsl.android.geophotoapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by alachman on 06.05.2018.
 */
class PhotoFilter : Serializable {
    companion object {
        const val DATE_TYPE = "dateType"
        const val EXPOSURES = "exposures"
        const val APERTURES = "apertures"
        const val DEVICES = "devices"
        const val FOCAL_LENGTHS = "focalLengths"
        const val AUTHORS = "authors"
        const val PHOTO_FILTER = "photoFilter"
    }

    @SerializedName("sort")
    var dateType: DateFilterType? = null
    @SerializedName("exposure")
    var exposures: ArrayList<String> = ArrayList()
    @SerializedName("aperture")
    var apertures: ArrayList<String> = ArrayList()
    @SerializedName("cameraName")
    var devices: ArrayList<String> = ArrayList()
    @SerializedName("focalLength")
    var focalLengths: ArrayList<String> = ArrayList()
    @SerializedName("author")
    var authors: ArrayList<String> = ArrayList()

    fun resetFilter() {
        dateType = null
        exposures = ArrayList()
        apertures = ArrayList()
        devices = ArrayList()
        focalLengths = ArrayList()
        authors = ArrayList()
    }
}
