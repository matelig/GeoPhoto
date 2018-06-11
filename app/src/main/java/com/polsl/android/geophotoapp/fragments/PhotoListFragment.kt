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
import com.squareup.picasso.Target
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_photo_list.*
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class PhotoListFragment : Fragment(), FetchPhotoNetworkingDelegate {

    private var networking: PhotoNetworking? = null
    private var exifNetworking: ExifNetworking? = null
    private var downloadSubject = PublishSubject.create<String>()
    private var compositeDisposable = CompositeDisposable()
    private var targets = ArrayList<Target>()
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

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
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
        var counter = 0
        if (compositeDisposable.isDisposed)
            compositeDisposable = CompositeDisposable()
        downloadSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    counter++
                    if (counter == photos!!.size)
                        finishDownloading()
                })
                .addTo(compositeDisposable)
        if (photos != null)
            for (photo in photos!!)
                downloadPhoto(photo)
    }

    private fun downloadPhoto(photo: Photo) {
        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", UserDataSharedPrefsHelper(context).getAccessToken())
                            .build()
                    chain.proceed(newRequest)
                }
                .build()
        val photoUrl = GeoPhotoEndpoints.URL + "displayPhoto?photoId=" + photo.photoId
        val picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(client)).build()
        val target = getTarget(photo.photoId.toString())
        picasso.load(photoUrl)
                .into(target)
        targets.add(target)
    }

    private fun getTarget(url: String): com.squareup.picasso.Target {
        return object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.d("Target", "On error " + url)
            }

            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                downloadSubject.onNext(downloadImage(bitmap))
            }

            private fun downloadImage(bitmap: Bitmap): String {
                var result = url
                val folder = File(Environment.getExternalStorageDirectory().getPath() + "/" + "GEOPHOTO")
                if (!folder.exists())
                    folder.mkdirs()
                val file = File(folder, "/" + System.currentTimeMillis() + "_" + url + ".jpg")
                try {
                    file.createNewFile()
                    Log.d("Target", "Image download " + url)
                    val ostream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream)
                    ostream.flush()
                    ostream.close()
                } catch (e: IOException) {
                    Timber.e(e.getLocalizedMessage())
                } finally {
                    Log.d("Target", "Finally  " + url)
                    return result
                }
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d("Target", "On prepare load " + url)
            }
        }
    }

    private fun finishDownloading() {
        (activity as BaseActivity).hideProgressDialog()
        (activity as BaseActivity).displayToast(R.string.download_finished)
        compositeDisposable.clear()
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