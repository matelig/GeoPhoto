package com.polsl.android.geophotoapp.util.photoMarkerUtils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class PhotoCluster(var id: String, var latLng: LatLng?, var miniature: ByteArray?): ClusterItem {
    override fun getSnippet(): String? {
        return null
    }

    override fun getTitle(): String? {
        return id
    }

    override fun getPosition(): LatLng? {
        return latLng
    }
}