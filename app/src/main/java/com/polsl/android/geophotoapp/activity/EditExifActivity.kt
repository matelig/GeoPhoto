package com.polsl.android.geophotoapp.activity

import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.dialog.MapDialog
import com.polsl.android.geophotoapp.dialog.MapDialogDelegate
import kotlinx.android.synthetic.main.activity_edit_exif.*
import com.google.android.gms.maps.model.LatLng
import com.polsl.android.geophotoapp.model.SelectablePhotoModel
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import com.polsl.android.geophotoapp.viewholder.PhotoViewHolder
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient


class EditExifActivity : BaseActivity(), MapDialogDelegate{

    var mapDialog = MapDialog()
    var photoId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exif)
        photoId = intent.getLongExtra("photoId", 0)
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
}

