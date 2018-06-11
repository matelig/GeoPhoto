package com.polsl.android.geophotoapp.model

import android.support.media.ExifInterface

data class Photo(val url: String, val thumbnailUrl: String? = null, val photoId: Long, var exif: ExifInterface? = null) {
    constructor(photoUrl: String, photoId: Long) : this(photoUrl, photoUrl, photoId)
}