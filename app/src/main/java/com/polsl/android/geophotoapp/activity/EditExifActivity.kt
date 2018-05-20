package com.polsl.android.geophotoapp.activity

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.Services.networking.ExifNetworking
import com.polsl.android.geophotoapp.Services.networking.ExifNetworkingDelegate
import com.polsl.android.geophotoapp.dialog.MapDialog
import com.polsl.android.geophotoapp.dialog.MapDialogDelegate
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.rest.restResponse.ExifParams
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_exif.*
import okhttp3.OkHttpClient


class EditExifActivity : BaseActivity(), MapDialogDelegate, ExifNetworkingDelegate {

    var mapDialog = MapDialog()
    var photoId: Long = 0L
    private var networking = ExifNetworking(this)
    private var exifParams: ExifParams? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exif)
        networking.delegate = this
        photoId = intent.getLongExtra("photoId", 0)
        mapDialog.delegate = this
        setupButtonsAction()
        setupPhotoImage()
        fetchExifParams()
        fillEditTexts()
    }

    override fun onOkButtonClick(location: LatLng) {
        //TODO: store this property in photo exif
        this.exifParams?.latitude = location.latitude
        this.exifParams?.longitude = location.longitude
        latitudeTv.text = location.latitude.toString()
        longitudeTv.text = location.longitude.toString()
        displayToast("OK clicked")
    }

    override fun onCancelButtonClick() {
        displayToast("Cancel clicked")
    }

    private fun fillEditTexts() {
        modelEdit.setText(exifParams?.cameraName, TextView.BufferType.EDITABLE)
        focalLengthEdit.setText(exifParams?.focalLength, TextView.BufferType.EDITABLE)
        apertureEdit.setText(exifParams?.maxAperture, TextView.BufferType.EDITABLE)
        exposureEdit.setText(exifParams?.exposure, TextView.BufferType.EDITABLE)
        latitudeTv.text = exifParams?.latitude.toString()
        longitudeTv.text = exifParams?.longitude.toString()
    }

    private fun setupButtonsAction() {
        locationButton.setOnClickListener {
            exifParams?.longitude?.let { longitude ->
                exifParams?.latitude?.let { latitude ->
                    mapDialog.location = LatLng(latitude, longitude)
                }
            }
            mapDialog.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_Panel)
            mapDialog.show(supportFragmentManager,"mapFragment")
        }

        applyExifChanges.setOnClickListener {
            exifParams?.cameraName = modelEdit.text.toString()
            exifParams?.focalLength = focalLengthEdit.text.toString()
            exifParams?.maxAperture = apertureEdit.text.toString()
            exifParams?.exposure = exposureEdit.text.toString()
            exifParams?.let { params ->
                networking.updateExifParams(params, photoId)
            } ?: displayToast("Error while sending exif params")

        }
    }

    private fun setupPhotoImage() {
        val photoUrl = GeoPhotoEndpoints.URL + "displayPhoto?photoId=" + this.photoId
        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", UserDataSharedPrefsHelper(this).getAccessToken()!!)
                            .build()
                    chain.proceed(newRequest)
                }
                .build()
        val picasso = Picasso.Builder(this).downloader(OkHttp3Downloader(client)).build()
        picasso.load(photoUrl)
                .placeholder(R.drawable.no_photo)
                .into(photoPreview)
    }

    private fun fetchExifParams() {
        networking.getExifParameters(this.photoId)
    }

    override fun error(error: String?) {
        error?.let {
            displayToast(it)
        }
        finish()
    }

    override fun success(params: ExifParams) {
        this.exifParams = params
    }

    override fun uploadSuccessful() {
        displayToast("Exif params successfully uploaded")
        finish()
    }


}

