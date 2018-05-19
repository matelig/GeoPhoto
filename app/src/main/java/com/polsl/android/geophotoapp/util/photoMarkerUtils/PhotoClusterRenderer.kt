package com.polsl.android.geophotoapp.util.photoMarkerUtils

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_exif.*
import okhttp3.OkHttpClient

class PhotoClusterRenderer(var context: Context, var gMap: GoogleMap, var clusterManager: ClusterManager<PhotoCluster>, var layoutInflater: LayoutInflater): DefaultClusterRenderer<PhotoCluster>(context, gMap, clusterManager) {

    private val iconGenerator = IconGenerator(context.applicationContext)
    private val iconClusterGenerator = IconGenerator(context.applicationContext)
    private val mImageView: ImageView
    private val dimension: Int = 50
    var markerLayout = layoutInflater.inflate(R.layout.marker_window_layout, null)
    init {
        var padding = 2
        mImageView = markerLayout.findViewById(R.id.markerImage)

    }

    override fun onBeforeClusterItemRendered(item: PhotoCluster, markerOptions: MarkerOptions?) {
        iconGenerator.setContentView(markerLayout)
        val photoUrl = GeoPhotoEndpoints.URL + "miniature?photoId=" + item.id
        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", UserDataSharedPrefsHelper(context).getAccessToken()!!)
                            .build()
                    chain.proceed(newRequest)
                }
                .build()
        val picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(client)).build()
        picasso.load(photoUrl)
                .placeholder(R.drawable.no_photo)
                .into(mImageView)
        //mImageView.setImageResource(R.drawable.location)
        var icon = iconGenerator.makeIcon()
        markerOptions?.icon(BitmapDescriptorFactory.fromBitmap(icon))?.title(item.id)
    }
//
    override fun onBeforeClusterRendered(cluster: Cluster<PhotoCluster>?, markerOptions: MarkerOptions?) {
    iconClusterGenerator.setBackground(ContextCompat.getDrawable(context, R.drawable.cluster_background_circle))
    iconClusterGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance)
        var icon: Bitmap = iconClusterGenerator.makeIcon(cluster?.size.toString())
        markerOptions?.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun shouldRenderAsCluster(cluster: Cluster<PhotoCluster>): Boolean {
        return cluster.size>1
    }

}