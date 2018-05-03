package com.polsl.android.geophotoapp.activity

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.media.ExifInterface
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.WindowManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.Services.LocationProvider
import com.polsl.android.geophotoapp.Services.LocationProviderDelegate
import com.polsl.android.geophotoapp.util.Exif
import com.polsl.android.geophotoapp.dialog.ProgressDialog
import kotlinx.android.synthetic.main.activity_taken_photo.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class TakenPhotoActivity : BaseActivity(), LocationProviderDelegate {

    var photoUri: String? = null
    var photoPath: String? = null
    var bitmap: Bitmap? = null
    var progressDialog = ProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taken_photo)
        photoUri = intent.getStringExtra("photoUrl")
        progressDialog.isCancelable = false
        //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
        val cursor = this.contentResolver.query(Uri.parse(this.photoUri),
                Array(1) { android.provider.MediaStore.Images.ImageColumns.DATA },
                null, null, null)
        cursor.moveToFirst()
        photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
        val uri = Uri.fromFile(file)
        handleOrientation(uri)
        capturedPhoto.setImageBitmap(bitmap)
        setupLocationButton()
    }

    @Throws(IOException::class)
    private fun getBytes(uri: Uri): ByteArray? {
        val iStream = contentResolver.openInputStream(uri)
        try {
            return getBytes(iStream)
        } finally {
            try {
                iStream.close()
            } catch (ignored: IOException) {
            }

        }
    }

    @Throws(IOException::class)
    private fun getBytes(inputStream: InputStream): ByteArray? {

        var bytesResult: ByteArray? = null
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        try {
            var len: Int
            len = inputStream.read(buffer)
            while (len != -1) {
                byteBuffer.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            bytesResult = byteBuffer.toByteArray()
        } finally {
            try {
                byteBuffer.close()
            } catch (ignored: IOException) {
                Log.e("TAG", ignored.message)
            }
        }
        return bytesResult
    }

    private fun handleOrientation(uri: Uri) {
        val photoBytes = getBytes(uri)
        photoPath?.let {
            val orientation = Exif.getOrientation(photoBytes)
            bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes!!.size, BitmapFactory.Options());
            bitmap = handleOrientation(orientation, bitmap)
        }
    }

    private fun handleOrientation(orientation: Int, bitmap: Bitmap?): Bitmap? {
        if (bitmap == null)
            return bitmap
        var rotatedBitmap = bitmap
        when (orientation) {
            90 -> rotatedBitmap = rotateImage(rotatedBitmap, 90f)
            180 -> rotatedBitmap = rotateImage(rotatedBitmap, 180f)
            270 -> rotatedBitmap = rotateImage(rotatedBitmap, 270f)
            else -> { }
        }
        return rotatedBitmap
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix,
                true)
    }

    private fun setupLocationButton() {
        provideLocationButton.setOnClickListener { locationButtonAction() }
    }

    private fun provideLocation() {
        var locationReader = LocationProvider(context = this)
        locationReader.delegate = this
        progressDialog.show(supportFragmentManager, "progress")
        locationReader.provideLocation()
    }

    private fun locationButtonAction() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : MultiplePermissionsListener {

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
        //TODO: zapisanie tej otrzymanej lokalizacji do exifu zdjęcia
        photoPath?.let {
            var exifParams = ExifInterface(it)
            var location = exifParams.latLong
            exifParams.setLatLong(currentLocation.latitude, currentLocation.longitude)
            //exifParams.setLatLong(51.5033640, 0.0) TODO: for testing uncomment this part
            exifParams.saveAttributes()
        }
        progressDialog.dismiss()
    }
}
