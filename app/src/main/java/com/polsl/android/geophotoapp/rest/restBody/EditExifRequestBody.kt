package com.polsl.android.geophotoapp.rest.restBody

import com.google.gson.annotations.SerializedName
import com.polsl.android.geophotoapp.rest.restResponse.ExifParams

class EditExifRequestBody(@field:SerializedName("photoParams")
                          var exifParams: ExifParams,
                          @field:SerializedName("photoId")
                          var photoId: Long) {
}