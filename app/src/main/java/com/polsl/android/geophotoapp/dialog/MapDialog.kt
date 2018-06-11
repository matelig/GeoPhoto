package com.polsl.android.geophotoapp.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.content.Context
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.polsl.android.geophotoapp.R
import kotlinx.android.synthetic.main.dialog_map.*

interface MapDialogDelegate {
    fun onOkButtonClick(location: LatLng)
    fun onCancelButtonClick()
}

class MapDialog : DialogFragment(), OnMapReadyCallback {

    var delegate: MapDialogDelegate? = null
    var location: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.dialog_map, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupButtonsAction()
        setupMap()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        location?.let { location ->
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(location))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 8.0F))
        }
        googleMap?.setOnMapClickListener {
            location = it
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(it))
        }
    }

    private fun setupButtonsAction() {
        okButton.setOnClickListener {
            location?.let {
                delegate?.onOkButtonClick(it)
                dialog.dismiss()
            }
            //TODO: show some alert when location is not selected
        }

        cancelButton.setOnClickListener {
            delegate?.onCancelButtonClick()
            dialog.dismiss()
        }
    }

    private fun setupMap() {
            var mMapView: MapView
            MapsInitializer.initialize(context)

            mMapView = dialog.findViewById(R.id.mapView)
            mMapView.onCreate(dialog.onSaveInstanceState())
            mMapView.onResume()
            mMapView.getMapAsync(this)
    }
}