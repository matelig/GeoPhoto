package com.polsl.android.geophotoapp.model

import android.support.media.ExifInterface

/**
 * Created by alachman on 29.04.2018.
 */
data class Photo(val url: String, val thumbnailUrl: String? = null, var exif: ExifInterface? = null) {
    constructor(photoUrl: String) : this(photoUrl, photoUrl)
}