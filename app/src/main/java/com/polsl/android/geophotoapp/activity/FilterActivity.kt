package com.polsl.android.geophotoapp.activity

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.adapter.FilterItemAdapter
import com.polsl.android.geophotoapp.model.DateFilterType
import com.polsl.android.geophotoapp.model.PhotoFilter
import com.polsl.android.geophotoapp.model.SelectableFilterModel
import kotlinx.android.synthetic.main.activity_filter.*

/**
 * Created by alachman on 04.05.2018.
 */
class FilterActivity : BaseActivity() {
    private var exposures: ArrayList<String>? = null
    private var apertures: ArrayList<String>? = null
    private var devices: ArrayList<String>? = null
    private var focalLengths: ArrayList<String>? = null
    private var photoFilter: PhotoFilter = PhotoFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setUpFiltersData()
        setUpDateRadioButtons()
    }

    private fun setUpDateRadioButtons() {
        dateAscendingRb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                photoFilter.dateType = DateFilterType.ASCENDING
        }
        dateDescendingRb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                photoFilter.dateType = DateFilterType.DESCENDING
        }
    }


    private fun setUpFiltersData() {
        exposures = intent.getStringArrayListExtra(PhotoFilter.EXPOSURES)
        apertures = intent.getStringArrayListExtra(PhotoFilter.APERTURES)
        devices = intent.getStringArrayListExtra(PhotoFilter.DEVICES)
        focalLengths = intent.getStringArrayListExtra(PhotoFilter.FOCAL_LENGTHS)
        setUpFilterAdapters()
    }

    private fun setUpFilterAdapters() {
        setUpAperturesAdapter()
        setUpDevicesAdapter()
        setUpExposuresAdapter()
        setUpFocalLengthsAdapter()
    }

    private fun setUpFocalLengthsAdapter() {
        val focalLengthAdapter = FilterItemAdapter(this)
        filterFocalLengthRv.layoutManager = GridLayoutManager(this, 4)
        focalLengthAdapter.items = getSelectableFilterModels(focalLengths) as ArrayList<Any>
        focalLengthAdapter.getItemClickObservable().subscribe({ t ->
            t as SelectableFilterModel
            if (t.isSelected)
                photoFilter.focalLengths.add(t.value)
            else
                photoFilter.focalLengths.remove(t.value)
        })
        filterFocalLengthRv.adapter = focalLengthAdapter
    }


    private fun getSelectableFilterModels(filterList: ArrayList<String>?): ArrayList<SelectableFilterModel>? {
        var selectableFilterModels = ArrayList<SelectableFilterModel>()
        for (filter in filterList!!)
            selectableFilterModels.add(SelectableFilterModel(filter, false))
        return selectableFilterModels
    }

    private fun setUpExposuresAdapter() {
        val exposuresAdapter = FilterItemAdapter(this)
        filterExposureRv.layoutManager = GridLayoutManager(this, 4)
        exposuresAdapter.items = getSelectableFilterModels(exposures) as ArrayList<Any>
        exposuresAdapter.getItemClickObservable().subscribe({ t ->
            t as SelectableFilterModel
            if (t.isSelected)
                photoFilter.exposures.add(t.value)
            else
                photoFilter.exposures.remove(t.value)
        })
        filterExposureRv.adapter = exposuresAdapter
    }

    private fun setUpDevicesAdapter() {
        val devicesAdapter = FilterItemAdapter(this)
        filterDeviceRv.layoutManager = GridLayoutManager(this, 4)
        devicesAdapter.items = getSelectableFilterModels(devices) as ArrayList<Any>
        devicesAdapter.getItemClickObservable().subscribe({ t ->
            t as SelectableFilterModel
            if (t.isSelected)
                photoFilter.devices.add(t.value)
            else
                photoFilter.devices.remove(t.value)
        })
        filterDeviceRv.adapter = devicesAdapter
    }

    private fun setUpAperturesAdapter() {
        val aperturesAdapter = FilterItemAdapter(this)
        filterApertureRv.layoutManager = GridLayoutManager(this, 4)
        aperturesAdapter.items = getSelectableFilterModels(apertures) as ArrayList<Any>
        aperturesAdapter.getItemClickObservable().subscribe({ t ->
            t as SelectableFilterModel
            if (t.isSelected)
                photoFilter.apertures.add(t.value)
            else
                photoFilter.apertures.remove(t.value)
        })
        filterApertureRv.adapter = aperturesAdapter
    }
}

