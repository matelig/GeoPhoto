package com.polsl.android.geophotoapp.activity

import android.Manifest
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.Services.LocationProvider
import com.polsl.android.geophotoapp.Services.LocationProviderDelegate
import kotlinx.android.synthetic.main.activity_taken_photo.*
import java.io.File

class TakenPhotoActivity : BaseActivity(), LocationProviderDelegate {

    var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taken_photo)
        photoPath = intent.getStringExtra("photoUrl")
        setupButtonsAction()
        processCapturedPhoto()
    }

    private fun setupButtonsAction() {
        processButton.setOnClickListener { processButtonClick() }
        dismissButton.setOnClickListener { dismissButtonClick() }
    }

    private fun processButtonClick() {
        //TODO: send this photo to server
        finish()
    }

    private fun dismissButtonClick() {
        //TODO: maybe remove this photo from storage?
        finish()
    }

    private fun processCapturedPhoto() {
        val cursor = this.contentResolver.query(Uri.parse(this.photoPath),
                Array(1) {android.provider.MediaStore.Images.ImageColumns.DATA},
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
        val uri = Uri.fromFile(file)
        capturedPhoto.setImageURI(uri)
        setupLocationButton()
    }

    private fun setupLocationButton() {
        provideLocationButton.setOnClickListener { locationButtonAction() }
    }

    private fun provideLocation() {
        var locationReader = LocationProvider(context = this)
        locationReader.delegate = this
        locationReader.provideLocation()
    }

    private fun locationButtonAction() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object: MultiplePermissionsListener {

                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            provideLocation()
                        } else {
                            Snackbar.make(takenPhotoLayout,
                                    R.string.storage_permission_denied_message,
                                    Snackbar.LENGTH_LONG)
                                    .show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        AlertDialog.Builder(this@TakenPhotoActivity)
                                .setTitle(R.string.storage_permission_rationale_title)
                                .setMessage(R.string.storage_permition_rationale_message)
                                .setNegativeButton(android.R.string.cancel,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.cancelPermissionRequest()
                                        })
                                .setPositiveButton(android.R.string.ok,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.continuePermissionRequest()
                                        })
                                .setOnDismissListener({ token?.cancelPermissionRequest() })
                                .show()
                    }


                }).check()
    }

    override fun locationReaded(currentLocation: Location) {
        print(currentLocation)
        //TODO: zapisanie tej otrzymanej lokalizacji do exifu zdjęcia
    }
}
