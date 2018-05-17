package com.polsl.android.geophotoapp.activity

import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.dialog.MapDialog
import com.polsl.android.geophotoapp.dialog.MapDialogDelegate
import kotlinx.android.synthetic.main.activity_edit_exif.*
import com.google.android.gms.maps.model.LatLng
import com.polsl.android.geophotoapp.Services.networking.ExifNetworking
import com.polsl.android.geophotoapp.Services.networking.ExifNetworkingDelegate
import com.polsl.android.geophotoapp.model.SelectablePhotoModel
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.rest.restResponse.ExifParams
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import com.polsl.android.geophotoapp.viewholder.PhotoViewHolder
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
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
    }

    override fun onOkButtonClick(location: LatLng) {
        //TODO: store this property in photo exif
        this.exifParams?.latitude = location.latitude
        this.exifParams?.longitude = location.longitude
        displayToast("OK clicked")
    }

    override fun onCancelButtonClick() {
        displayToast("Cancel clicked")
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
        exifParams = ExifParams()
        exifParams?.latitude = 50.425
        exifParams?.longitude = 13.253
    }

    override fun success(params: ExifParams) {
        this.exifParams = params
    }

}

