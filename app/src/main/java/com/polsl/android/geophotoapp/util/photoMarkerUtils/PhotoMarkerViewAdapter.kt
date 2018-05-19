package com.polsl.android.geophotoapp.util.photoMarkerUtils

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.polsl.android.geophotoapp.R

class PhotoMarkerViewAdapter(var layoutInflater: LayoutInflater): GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker?): View {
        var popup = layoutInflater.inflate(R.layout.marker_window_layout, null)

        return popup
    }

    override fun getInfoWindow(p0: Marker?): View {
        var popup = layoutInflater.inflate(R.layout.marker_window_layout, null)

        return popup
    }
}