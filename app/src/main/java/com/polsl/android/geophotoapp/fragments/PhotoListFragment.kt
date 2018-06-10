package com.polsl.android.geophotoapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.Services.networking.ExifNetworking
import com.polsl.android.geophotoapp.Services.networking.FetchPhotoNetworkingDelegate
import com.polsl.android.geophotoapp.Services.networking.PhotoNetworking
import com.polsl.android.geophotoapp.activity.BaseActivity
import com.polsl.android.geophotoapp.activity.EditExifActivity
import com.polsl.android.geophotoapp.activity.FilterActivity
import com.polsl.android.geophotoapp.activity.TabbedActivity
import com.polsl.android.geophotoapp.adapter.ImageRvAdapter
import com.polsl.android.geophotoapp.model.Photo
import com.polsl.android.geophotoapp.model.PhotoFilter
import com.polsl.android.geophotoapp.model.SelectablePhotoModel
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.rest.restResponse.ExifParams
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_photo_list.*
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

/**
 * Created by alachman on 29.04.2018.
 */
class PhotoListFragment : Fragment(), FetchPhotoNetworkingDelegate {
    private var downloadingPublishSubject = PublishSubject.create<String>()
    private var networking: PhotoNetworking? = null
    private var exifNetworking: ExifNetworking? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_photo_list, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networking = PhotoNetworking(context)
        networking?.delegateFetch = this
        exifNetworking = ExifNetworking(context)
        (activity as BaseActivity).showProgressDialog(getString(R.string.wait), getString(R.string.downloading))
        getPhotos()
        prepareDownloadButton()
        setUpFiltersObservable()
    }

    private fun setUpFiltersObservable() {
        (activity as TabbedActivity).filtersClickObservable.subscribe({ openFiltersActivity() })
    }

    private fun openFiltersActivity() {
        val intent = Intent(activity, FilterActivity::class.java)
        val bundle = Bundle()
        bundle.putStringArrayList(PhotoFilter.APERTURES, getApertures())
        bundle.putStringArrayList(PhotoFilter.FOCAL_LENGTHS, getFocalLengths())
        bundle.putStringArrayList(PhotoFilter.DEVICES, getDevicesName())
        bundle.putStringArrayList(PhotoFilter.EXPOSURES, getExposures())
        bundle.putStringArrayList(PhotoFilter.AUTHORS, getAuthors())
        if (photoFilter != null)
            bundle.putSerializable(PhotoFilter.PHOTO_FILTER, photoFilter)
        intent.putExtras(bundle)
        startActivityForResult(intent, TabbedActivity.FILTERS_CODE)
    }

    private fun prepareDownloadButton() {
        downloadPhotosButton.setOnClickListener({
            (activity as BaseActivity).showProgressDialog(getString(R.string.wait), getString(R.string.downloading))
            downloadPhotos()
        })
    }

    private fun downloadPhotos() {
        var photos = adapter?.getSelectedPhotos()
        var photoCounter = 0
        downloadingPublishSubject.subscribe({
            photoCounter++
            Log.d("Another", "Photo count " + photoCounter)
            if (photoCounter == photos!!.size)
                finishDownloading()
        })
        if (photos != null)
            for (photo in photos!!)
                downloadPhoto(photo)
    }

    private fun downloadPhoto(photo: Photo) {
        val photoUrl = GeoPhotoEndpoints.URL + "displayPhoto?photoId=" + photo.photoId
        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", UserDataSharedPrefsHelper(context).getAccessToken())
                            .build()
                    chain.proceed(newRequest)
                }
                .build()

        val picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(client)).build()
        picasso.load(photoUrl)
                .into(getTarget(photo.photoId.toString()))
    }

    //target to save
    private fun getTarget(url: String): com.squareup.picasso.Target {
        return object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.e("Error", "Bitmap failed")
                downloadingPublishSubject.onNext("error")
            }

            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                Thread(Runnable {
                    val folder = File(Environment.getExternalStorageDirectory().getPath() + "/" + "GeoPhoto")
                    if (!folder.exists())
                        folder.mkdirs()
                    val file = File(folder, "/" + System.currentTimeMillis().toString() + "_" + url + ".png")
                    try {
                        file.createNewFile()
                        val ostream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream)
                        ostream.flush()
                        ostream.close()
                        Log.d("Download", "Bitmap saved")
                        downloadingPublishSubject.onNext(url)
                    } catch (e: IOException) {
                        Timber.e(e.getLocalizedMessage())
                    }
                }).start()

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }
        }
    }

    private fun finishDownloading() {
        (activity as BaseActivity).hideProgressDialog()
        (activity as BaseActivity).displayToast(R.string.download_finished)
    }

    private var photos: List<Photo>? = null
    private var filteredPhotos: List<Photo>? = null
    var adapter: ImageRvAdapter? = null
    private var subscribe: Disposable? = null

    private fun getPhotos() {
        networking?.getPhotosId()
    }

    private fun preparePhotosAdapter() {
        adapter = ImageRvAdapter(activity)
        photosRv.layoutManager = GridLayoutManager(activity, 4)
        adapter!!.items = getSelectablePhotos() as ArrayList<Any>
        adapter!!.selectedItemsObservable.subscribe({ t ->
            if (t as Int > 0) {
                selectedPhotoLayout.visibility = View.VISIBLE
                selectedPhotosTv.text = getString(R.string.selected_photos, t)
            } else
                selectedPhotoLayout.visibility = View.GONE
        })
        photosRv.adapter = adapter
    }

    private fun getSelectablePhotos(): ArrayList<SelectablePhotoModel>? {
        var selectablePhotos = ArrayList<SelectablePhotoModel>()
        for (photo in filteredPhotos!!)
            selectablePhotos.add(SelectablePhotoModel(photo, false))
        return selectablePhotos
    }

    override fun acquired(photosId: List<Long>) {
        getExifParamsForPhotos(photosId)
        photos = getPhotosList(photosId)
        filteredPhotos = photos
        preparePhotosAdapter()
        setupItemClick()
    }

    override fun acquiredFilteredPhotos(result: List<Long>) {
        filteredPhotos = getPhotosList(result)
        preparePhotosAdapter()
    }

    private var savedExifParams: List<ExifParams> = ArrayList()

    private fun getExifParamsForPhotos(photosId: List<Long>) {
        getExifsForIds(photosId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ exifParams ->
                    savedExifParams = exifParams
                    (activity as BaseActivity).hideProgressDialog()
                }, {
                    error(it)
                    (activity as BaseActivity).hideProgressDialog()
                })
    }

    private fun getExifsForIds(photosId: List<Long>): Observable<List<ExifParams>> {
        var exifObservables = ArrayList<Observable<ExifParams>>()
        for (i in 0..photosId.size - 1)
            exifObservables.add(exifNetworking?.getExifParametersObservable(photosId[i])!!)
        return exifObservables.observableZip()
    }

    inline fun <reified T> List<Observable<T>>.observableZip(): Observable<List<T>> =
            when (isEmpty()) {
                true -> Observable.just(emptyList())
                else -> Observable.zip(this) { it.toList() }
                        .map {
                            @Suppress("UNCHECKED_CAST")
                            it as List<T>
                        }
            }

    override fun error(error: Throwable) {
        (activity as BaseActivity).hideProgressDialog()
        Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
    }

    private fun getPhotosList(ids: List<Long>): List<Photo> {
        var photos = ArrayList<Photo>()
        for (i in ids)
            photos.add(Photo(GeoPhotoEndpoints.geoPhotoApi.URL + "miniature?photoId=" + i, i))
        return photos
    }

    private fun setupItemClick() {
        subscribe = adapter?.getItemClickObservable()
                ?.subscribe({
                    val photoModel = it as? SelectablePhotoModel
                    photoModel?.photo?.let {
                        showEditExifActivity(it.photoId)
                    }
                })
    }

    private fun showEditExifActivity(photoId: Long) {
        val intent = Intent(this.context, EditExifActivity::class.java)
        intent.putExtra("photoId", photoId)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }

    private var photoFilter: PhotoFilter? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == TabbedActivity.FILTERS_CODE) {
            data?.let {
                photoFilter = it.extras.getSerializable(FilterActivity.FILTER_KEY) as PhotoFilter
                filterPhotos()
            }
        }
    }

    private fun filterPhotos() {
        networking?.getFilteredPhotos(photoFilter)
    }

    private fun getAuthors(): ArrayList<String?>? {
        return ArrayList(savedExifParams.filter { exifParams -> exifParams.author != null }
                .map { exifParams -> exifParams.author }
                .distinct().toList())
    }

    private fun getDevicesName(): ArrayList<String?>? {
        return ArrayList(savedExifParams.filter { exifParams -> exifParams.cameraName != null }
                .map { exifParams -> exifParams.cameraName }
                .distinct().toList())

    }

    private fun getFocalLengths(): ArrayList<String?>? {
        return ArrayList(savedExifParams.filter { exifParams -> exifParams.focalLength != null }
                .map { exifParams -> exifParams.focalLength }
                .distinct().toList())
    }

    private fun getApertures(): ArrayList<String?>? {
        return ArrayList(savedExifParams.filter { exifParams -> exifParams.maxAperture != null }
                .map { exifParams -> exifParams.maxAperture }
                .distinct().toList())
    }

    private fun getExposures(): ArrayList<String?>? {
        return ArrayList(savedExifParams.filter { exifParams -> exifParams.exposure != null }
                .map { exifParams -> exifParams.exposure }
                .distinct().toList())
    }
}