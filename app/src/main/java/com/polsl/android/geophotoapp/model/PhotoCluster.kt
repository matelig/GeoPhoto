package com.polsl.android.geophotoapp.model

import android.media.Image
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class PhotoCluster(var name: String, var latLng: LatLng): ClusterItem {
    override fun getSnippet(): String? {
        return null
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getPosition(): LatLng {
        return latLng
    }
}