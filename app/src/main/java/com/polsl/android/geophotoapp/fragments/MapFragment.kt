package com.polsl.android.geophotoapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.polsl.android.geophotoapp.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val LATITUDE = "latitude"
private const val LONGITUDE = "longitude"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MapFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    // TODO: Rename and change types of parameters
    private var latitude: Double? = null
    private var longitude: Double? = null
    //private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble(LATITUDE)
            longitude = it.getDouble(LONGITUDE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(
                R.layout.fragment_map, container, false)
        // rootView.findViewById(R.id.map) as SupportMapFragment
        //mapFragment.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val actualLocation = LatLng(latitude!!, longitude!!)
        googleMap?.addMarker(MarkerOptions().position(actualLocation)
                .title("Your location"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(actualLocation))
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
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param latitude
         * @param longitude
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(latitude: Double, longitude: Double): MapFragment =
                MapFragment().apply {
                    arguments = Bundle().apply {
                        putDouble(LATITUDE, latitude)
                        putDouble(LONGITUDE, longitude)
                    }
                }
    }
}
