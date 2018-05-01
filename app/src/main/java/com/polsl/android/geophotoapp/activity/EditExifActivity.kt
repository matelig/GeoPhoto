package com.polsl.android.geophotoapp.activity

import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.dialog.MapDialog
import com.polsl.android.geophotoapp.dialog.MapDialogDelegate
import kotlinx.android.synthetic.main.activity_edit_exif.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng


class EditExifActivity : BaseActivity(), MapDialogDelegate{

    var mapDialog = MapDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exif)
        mapDialog.delegate = this
        setupButtonsAction()
    }

    override fun onOkButtonClick(location: LatLng) {
        //TODO: store this property in photo exif
        displayToast("OK clicked")
    }

    override fun onCancelButtonClick() {
        displayToast("Cancel clicked")
    }

    private fun setupButtonsAction() {
        locationButton.setOnClickListener {
            mapDialog.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_Panel)
            mapDialog.show(supportFragmentManager,"mapFragment")
        }
    }
}

