package com.polsl.android.geophotoapp.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.media.ExifInterface
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.activity.EditExifActivity
import com.polsl.android.geophotoapp.activity.FilterActivity
import com.polsl.android.geophotoapp.activity.TabbedActivity
import com.polsl.android.geophotoapp.adapter.ImageRvAdapter
import com.polsl.android.geophotoapp.model.Photo
import com.polsl.android.geophotoapp.model.PhotoFilter
import com.polsl.android.geophotoapp.model.SelectablePhotoModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_photo_list.*

/**
 * Created by alachman on 29.04.2018.
 */
class PhotoListFragment : Fragment() {

    //todo message when there is no internet
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_photo_list, container, false)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        intent.putExtras(bundle)
        startActivityForResult(intent, TabbedActivity.FILTERS_CODE)
    }

    private fun prepareDownloadButton() {

        downloadPhotosButton.setOnClickListener(View.OnClickListener {
            //todo
//            downloadPhotos()
            Toast.makeText(activity, "Photos downloaded", Toast.LENGTH_SHORT).show()
        })
    }

    private var photos: List<Photo>? = null
    var adapter: ImageRvAdapter? = null
    private var subscribe: Disposable? = null

    private fun getPhotos() {
        //todo get photos list from server
        photos = getPhotosList()
        preparePhotosAdapter()
        setupItemClick()
    }

    private fun preparePhotosAdapter() {
        adapter = ImageRvAdapter(activity)
        photosRv.layoutManager = GridLayoutManager(activity, 4)
        adapter!!.items = getSelectablePhotos() as ArrayList<Any>
        adapter!!.getItemClickObservable().subscribe({ t ->
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
        for (photo in photos!!)
            selectablePhotos.add(SelectablePhotoModel(photo, false))
        return selectablePhotos
    }

    private fun getPhotosList(): List<Photo> {
        var photos = ArrayList<Photo>()
        for (i in 0..200)
            photos.add(Photo("https://picsum.photos/200/?image=" + i))
        return photos
    }

    private fun setupItemClick() {
        subscribe = adapter?.getItemClickObservable()
                ?.subscribe({
                    var photoModel = it as? SelectablePhotoModel
                    photoModel?.photo?.let {
                        Toast.makeText(this.context, "Clicked on ${it.url}", Toast.LENGTH_LONG).show()
                        showEditExifActivity(it.url)
                    }
                })
    }

    private fun showEditExifActivity(photoId: String) {
        val intent = Intent(this.context, EditExifActivity::class.java)
        intent.putExtra("photoUrl", photoId)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == TabbedActivity.FILTERS_CODE) {
            //handle filters
        }
    }

    fun getDevicesName(): ArrayList<String?>? {
        var test = ArrayList<String?>()
        test.add("Samsung TM-78")
        test.add("Xiaomi Red Mi")
        return test
       // return photos?.map { photo -> photo.exif?.getAttribute(ExifInterface.TAG_MODEL) }?.toList() as ArrayList<String?>
    }

    fun getFocalLengths(): ArrayList<String?>? {
        var test = ArrayList<String?>()
        test.add("413/100")
        test.add("450/100")
        return test
        //todo uncomment
        // return photos?.map { photo -> photo.exif?.getAttribute(ExifInterface.TAG_FOCAL_LENGTH) }?.toList() as ArrayList<String?>
    }

    fun getApertures(): ArrayList<String?>? {
        var test = ArrayList<String?>()
        test.add("228/100")
        test.add("230/100")
        return test
        // return photos?.map { photo -> photo.exif?.getAttribute(ExifInterface.TAG_APERTURE_VALUE) }?.toList() as ArrayList<String?>
    }

    fun getExposures(): ArrayList<String?>? {
        var test = ArrayList<String?>()
        test.add("0.03")
        test.add("1")
        return test
        //return photos?.map { photo -> photo.exif?.getAttribute(ExifInterface.TAG_EXPOSURE_PROGRAM) }?.toList() as ArrayList<String?>
    }
}