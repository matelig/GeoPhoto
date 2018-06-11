package com.polsl.android.geophotoapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Base64
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
import com.polsl.android.geophotoapp.Services.networking.PhotoLocationNetworking
import com.polsl.android.geophotoapp.Services.networking.PhotoLocationNetworkingDelegate
import com.polsl.android.geophotoapp.rest.restResponse.PhotoLocation

class MapFragment : Fragment(), OnMapReadyCallback, PhotoLocationNetworkingDelegate {

    var gMap: GoogleMap? = null
    var photoClusters = listOf<PhotoCluster>()
    var clusterManager: ClusterManager<PhotoCluster>? = null
    var photoNetworking: PhotoLocationNetworking? = null
    var savedInstanceState: Bundle? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(
                R.layout.fragment_map, container, false)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        photoNetworking = PhotoLocationNetworking(context)
        photoNetworking?.delegate = this
        photoNetworking?.fetchPhotoWithLocation()
        photoClusters = listOf()
        this.savedInstanceState = savedInstanceState

    }

    fun getMapsAsync() {
        var mMapView: SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        MapsInitializer.initialize(context)
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
        clusterManager?.addItems(photoClusters)
        clusterManager?.cluster()
        clusterManager?.setOnClusterClickListener({
            Toast.makeText(context, "Cluster click", Toast.LENGTH_SHORT).show()
            false
        })

        clusterManager?.setOnClusterItemClickListener(
                {
                    Toast.makeText(context, "Cluster item click", Toast.LENGTH_SHORT).show()

                    false
                })
        gMap?.setOnMarkerClickListener(clusterManager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gMap?.clear()
    }

    override fun success(list: List<PhotoLocation>) {
        for (photo in list) {
            photo.latitude?.let {latitude ->
                photo.longitude?.let { longitude->
                    var cluster = PhotoCluster(photo.photoId.toString(), LatLng(latitude, longitude), Base64.decode(photo.miniature, Base64.DEFAULT))
                    photoClusters += cluster
                }
            }
        }
        getMapsAsync()
    }

    override fun error(error: String?) {
        print("error")
        getMapsAsync()
    }


}
