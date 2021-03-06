package com.polsl.android.geophotoapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.Services.networking.PhotoNetworking
import com.polsl.android.geophotoapp.Services.networking.UploadPhotoNetworkingDelegate
import com.polsl.android.geophotoapp.activity.BaseActivity
import com.polsl.android.geophotoapp.adapter.GalleryImageRvAdapter
import com.polsl.android.geophotoapp.model.Photo
import com.polsl.android.geophotoapp.model.SelectablePhotoModel
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_gallery_photos.*
import java.io.File


class GalleryPhotosFragment : Fragment(), UploadPhotoNetworkingDelegate {

    private lateinit var photos: List<String>
    var adapter: GalleryImageRvAdapter? = null
    private var subscribe: Disposable? = null
    var networking: PhotoNetworking? = null
    var user: String? = null


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = UserDataSharedPrefsHelper(activity).getLoggedUser()?.username
        photos = getImagePaths(context)
        networking = PhotoNetworking(context)
        networking?.delegateUpload = this
        preparePhotosAdapter()
        setupItemClick()
        prepareUploadButton()
    }

    private fun prepareUploadButton() {
        uploadPhotosButton.setOnClickListener({
            uploadSelectedPhotos()
        })
    }

    private fun uploadSelectedPhotos() {
        (activity as BaseActivity).showProgressDialog(getString(R.string.wait), getString(R.string.uploading))
        for (photo in adapter!!.items!!) {
            if ((photo as SelectablePhotoModel).isSelected) {
                networking?.uploadPhoto(File(photo.photo.thumbnailUrl))
            }
        }
    }

    private fun preparePhotosAdapter() {
        adapter = GalleryImageRvAdapter(activity)
        galleryPhotosRv.layoutManager = GridLayoutManager(activity, 4)
        adapter!!.items = getSelectablePhotos() as ArrayList<Any>
        adapter!!.selectedItemsObservable.subscribe({ t ->
            if (t > 0) {
                selectedPhotoLayout.visibility = View.VISIBLE
                selectedPhotosTv.text = getString(R.string.selected_photos, t)
            } else
                selectedPhotoLayout.visibility = View.GONE
        })
        galleryPhotosRv.adapter = adapter
    }

    private fun getSelectablePhotos(): ArrayList<SelectablePhotoModel>? {
        var selectablePhotos = ArrayList<SelectablePhotoModel>()
        for (photo in photos)
            selectablePhotos.add(SelectablePhotoModel(Photo(photo, 0), false))
        return selectablePhotos
    }

    private fun setupItemClick() {
        subscribe = adapter?.getItemClickObservable()
                ?.subscribe({
                    var photoModel = it as? SelectablePhotoModel
                    photoModel?.photo?.let {
                        //Toast.makeText(this.context, "Clicked on ${it.url}", Toast.LENGTH_LONG).show()
                        //var imageExif = ExifInterface(it.thumbnailUrl!!)
                        //var model = imageExif.getAttribute(ExifInterface.TAG_MODEL)
                        //var make = imageExif.getAttribute(ExifInterface.TAG_MAKE)
                        //Toast.makeText(activity, "$model $make", Toast.LENGTH_SHORT).show()
                        //var location = imageExif.latLong
                        //Toast.makeText(context, "Latitude ${location?.get(0)}, longitude ${location?.get(1)}", Toast.LENGTH_SHORT).show()
                        //showEditExifActivity(it.url)
                    }
                })
    }

    private fun getImagePaths(context: Context): List<String> {
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED)

        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        )
        val result = ArrayList<String>(cursor!!.count)

        if (cursor.moveToFirst()) {
            val imagePath = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            do {
                result.add(cursor.getString(imagePath))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery_photos, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }

    override fun success() {
        selectedPhotoLayout.visibility = View.GONE
        (activity as BaseActivity).hideProgressDialog()
        Toast.makeText(context, "Photo uploaded", Toast.LENGTH_SHORT).show()
    }

    override fun error(error: Throwable) {
        (activity as BaseActivity).hideProgressDialog()
        error.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}
