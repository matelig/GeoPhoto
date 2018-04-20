package com.polsl.android.geophotoapp.Services

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log

interface  LocationProviderDelegate {
    fun locationReaded(currentLocation: Location)
}

class LocationProvider(context: Context) {

    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null
    private var context = context
    var delegate: LocationProviderDelegate? = null

    @SuppressLint("MissingPermission")
    fun provideLocation() {
        locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager?

        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener, null)
        } catch (ex: SecurityException) {
            Log.d("tag", "Security Exception, no location available")
        }

    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationManager?.removeUpdates(this)
            delegate?.locationReaded(location)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}