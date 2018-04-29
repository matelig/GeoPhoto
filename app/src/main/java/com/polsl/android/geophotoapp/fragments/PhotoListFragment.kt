package com.polsl.android.geophotoapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.adapter.ImageRvAdapter
import com.polsl.android.geophotoapp.model.Photo
import com.polsl.android.geophotoapp.model.SelectablePhotoModel
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
    }

    private fun prepareDownloadButton() {

        downloadPhotosButton.setOnClickListener(View.OnClickListener {
            //todo
//            downloadPhotos()
            Toast.makeText(activity, "Photos downloaded", Toast.LENGTH_SHORT).show()
        })
    }

    private lateinit var photos: List<String>

    private fun getPhotos() {
        //todo get photos list from server
        photos = getPhotosList()
        preparePhotosAdapter()
    }

    private fun preparePhotosAdapter() {
        val adapter = ImageRvAdapter(activity)
        photosRv.layoutManager = GridLayoutManager(activity, 4)
        adapter.items = getSelectablePhotos() as ArrayList<Any>
        adapter.selectedItemsObservable.subscribe({ t ->
            if (t > 0) {
                selectedPhotoLayout.visibility = View.VISIBLE
                selectedPhotosTv.text = getString(R.string.selected_photos, t)
            } else
                selectedPhotoLayout.visibility = View.GONE
        })
        photosRv.adapter = adapter
    }

    private fun getSelectablePhotos(): ArrayList<SelectablePhotoModel>? {
        var selectablePhotos = ArrayList<SelectablePhotoModel>()
        for (photo in photos)
            selectablePhotos.add(SelectablePhotoModel(Photo(photo), false))
        return selectablePhotos
    }

    private fun getPhotosList(): List<String> {
        var photos = ArrayList<String>()
        for (i in 0..200)
            photos.add("https://picsum.photos/200/?image=" + i)
        return photos
    }
}