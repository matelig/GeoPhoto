package com.polsl.android.geophotoapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.util.photoMarkerUtils.PhotoCluster
import com.polsl.android.geophotoapp.util.photoMarkerUtils.PhotoClusterRenderer
import android.widget.Toast
import com.polsl.android.geophotoapp.util.photoMarkerUtils.PhotoMarkerViewAdapter


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

        var clusterRenderer = PhotoClusterRenderer(context, gMap!!, clusterManager!!, LayoutInflater.from(context))

        clusterManager?.renderer = clusterRenderer

        googleMap?.setOnCameraIdleListener(clusterManager)

        for (loca in locations) {
            clusterManager?.addItem(PhotoCluster("2", loca))
        }

        clusterManager?.cluster()

        //clusterManager?.markerCollection?.setOnInfoWindowAdapter(PhotoMarkerViewAdapter(LayoutInflater.from(context)))

        clusterManager?.setOnClusterClickListener({
            Toast.makeText(context, "Cluster click", Toast.LENGTH_SHORT).show()
            // if true, do not move camera
            false
        })

        clusterManager?.setOnClusterItemClickListener(
                {
                    Toast.makeText(context, "Cluster item click", Toast.LENGTH_SHORT).show()

                    // if true, click handling stops here and do not show info view, do not move camera
                    // you can avoid this by calling:
                    // renderer.getMarker(clusterItem).showInfoWindow();

                    false
                })
        gMap?.setOnMarkerClickListener(clusterManager)
        //gMap?.setInfoWindowAdapter(clusterManager?.markerManager)
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
