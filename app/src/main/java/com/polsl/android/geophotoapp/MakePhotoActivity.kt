package com.polsl.android.geophotoapp


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import butterknife.BindView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.polsl.android.geophotoapp.Services.LocationProvider
import com.polsl.android.geophotoapp.Services.LocationProviderDelegate
import kotlinx.android.synthetic.main.activity_make_photo.*
import java.io.File


class MakePhotoActivity : AppCompatActivity(), LocationProviderDelegate {

    @BindView(R.id.cameraMainContainer)
    var mainContainer: ConstraintLayout? = null

    private val TAKE_PHOTO_REQUEST = 101
    private var mCurrentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_photo)
        cameraPreview.setOnClickListener { validatePermission() }
        locationButton.visibility = View.INVISIBLE
    }

    private fun validatePermission() {

        Dexter.withActivity(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(object : MultiplePermissionsListener {

            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    launchCamera()
                } else {
                    Snackbar.make(mainContainer!!,
                            R.string.storage_permission_denied_message,
                            Snackbar.LENGTH_LONG)
                            .show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                AlertDialog.Builder(this@MakePhotoActivity)
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

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null) {
            mCurrentPhotoPath = fileUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            processCapturedPhoto()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun processCapturedPhoto() {
        val cursor = contentResolver.query(Uri.parse(mCurrentPhotoPath),
                Array(1) {android.provider.MediaStore.Images.ImageColumns.DATA},
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
        val uri = Uri.fromFile(file)
        cameraPreview.setImageURI(uri)
        changePreviewParams()
        setupLocationButton()
    }

    private fun changePreviewParams() {
        cameraPreview.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        cameraPreview.setOnClickListener(null)
    }

    private fun setupLocationButton() {
        locationButton.visibility = View.VISIBLE
        locationButton.setOnClickListener { locationButtonAction() }
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
                    Snackbar.make(mainContainer!!,
                            R.string.storage_permission_denied_message,
                            Snackbar.LENGTH_LONG)
                            .show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                AlertDialog.Builder(this@MakePhotoActivity)
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