package com.polsl.android.geophotoapp.fragments

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.activity.TabbedActivity
import com.polsl.android.geophotoapp.model.PhotoCluster
import kotlinx.android.synthetic.main.fragment_make_photo.*
import kotlinx.android.synthetic.main.fragment_map.*



class MapFragment : Fragment(), OnMapReadyCallback {

    var locations = prepareLocation()
    var gMap: GoogleMap? = null

    var clusterManager: ClusterManager<PhotoCluster>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(
                R.layout.fragment_map, container, false)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var mMapView: SupportMapFragment
        MapsInitializer.initialize(context)

        mMapView = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()
        mMapView.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        gMap = googleMap
        clusterManager = ClusterManager(context, gMap!!)
        googleMap?.setOnCameraIdleListener(clusterManager)

        for (loca in locations) {
            clusterManager?.addItem(PhotoCluster("name", loca))
        }

        clusterManager?.cluster()

//        val actualLocation = LatLng(latitude!!, longitude!!)
//        googleMap?.addMarker(MarkerOptions().position(actualLocation)
//                .title("Your location"))
//        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(actualLocation))
    }

    fun prepareLocation(): List<LatLng> {
        var location = listOf<LatLng>()
        for (i in 0..9) {
            val latLng = LatLng((-34 + i).toDouble(), (151 + i).toDouble())
            location += latLng
        }

        return location
    }
}
