package com.polsl.android.geophotoapp.fragments

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.adapter.ImageAdapter
import kotlinx.android.synthetic.main.fragment_gallery_photos.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [GalleryPhotosFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GalleryPhotosFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class GalleryPhotosFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
        }
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery_photos, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imagePaths = getImagePaths(context)
        photosGrid.adapter = ImageAdapter(context, imagePaths.size, imagePaths)
        photosGrid.onItemClickListener =
                AdapterView.OnItemClickListener { _, v, position, _ ->
                    Toast.makeText(context, imagePaths[position], Toast.LENGTH_SHORT).show()
                }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        //listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        //fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GalleryPhotosFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                GalleryPhotosFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}
