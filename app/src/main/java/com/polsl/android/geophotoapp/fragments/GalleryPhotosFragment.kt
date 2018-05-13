package com.polsl.android.geophotoapp.fragments

import android.content.Context
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
import com.polsl.android.geophotoapp.adapter.GalleryImageRvAdapter
import com.polsl.android.geophotoapp.model.Photo
import com.polsl.android.geophotoapp.model.SelectablePhotoModel
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_gallery_photos.*
import okhttp3.Credentials
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class GalleryPhotosFragment : Fragment() {

    private lateinit var photos: List<String>
    var adapter: GalleryImageRvAdapter? = null
    private var subscribe: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery_photos, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photos = getImagePaths(context)
        preparePhotosAdapter()
        setupItemClick()
        prepareUploadButton()
    }

    private fun prepareUploadButton() {
        uploadPhotosButton.setOnClickListener(View.OnClickListener {
            uploadSelectedPhotos()
            Toast.makeText(activity, "Photos uploaded", Toast.LENGTH_SHORT).show()
        })
    }

    //todo: check if it even works
    private fun uploadSelectedPhotos() {
        val userData = UserDataSharedPrefsHelper(activity).getLoggedUser()
        val apiService = GeoPhotoEndpoints.create()
        for (photo in adapter!!.items!!) {
            if ((photo as SelectablePhotoModel).isSelected) {
                val basic = Credentials.basic(userData!!.username, userData!!.password)
                val file = File(photo.photo.thumbnailUrl)
                val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val body = MultipartBody.Part.createFormData("photo", file.name, reqFile)
                val upload = apiService.upoladPhoto(body, basic)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                upload.subscribe({ result ->
                    Toast.makeText(activity, "wysłano", Toast.LENGTH_SHORT).show()
                }, { error ->
                    Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
                })
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
            selectablePhotos.add(SelectablePhotoModel(Photo(photo), false))
        return selectablePhotos
    }

    private fun setupItemClick() {
        subscribe = adapter?.getItemClickObservable()
                ?.subscribe({
                    var photoModel = it as? SelectablePhotoModel
                    photoModel?.photo?.let {
                        //Toast.makeText(this.context, "Clicked on ${it.url}", Toast.LENGTH_LONG).show()
                        var imageExif = ExifInterface(it.url)
                        var location = imageExif.latLong
                        Toast.makeText(context, "Latitude ${location?.get(0)}, longitude ${location?.get(1)}", Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }

}
