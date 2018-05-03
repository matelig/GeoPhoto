package com.polsl.android.geophotoapp.activity

import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.dialog.MapDialog
import com.polsl.android.geophotoapp.dialog.MapDialogDelegate
import kotlinx.android.synthetic.main.activity_edit_exif.*
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso


class EditExifActivity : BaseActivity(), MapDialogDelegate{

    var mapDialog = MapDialog()
    var photoUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exif)
        photoUrl = intent.getStringExtra("photoUrl")
        mapDialog.delegate = this
        setupButtonsAction()
        setupPhotoImage()
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

    private fun setupPhotoImage() {
        Picasso.
                get().
                load(photoUrl).
                into(photoPreview)
    }
}

