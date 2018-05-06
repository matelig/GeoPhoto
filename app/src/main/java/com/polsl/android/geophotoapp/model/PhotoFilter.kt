package com.polsl.android.geophotoapp.model

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
    }

    var dateType: DateFilterType? = null
    var exposures: ArrayList<String> = ArrayList()
    var apertures: ArrayList<String> = ArrayList()
    var devices: ArrayList<String> = ArrayList()
    var focalLengths: ArrayList<String> = ArrayList()
}
