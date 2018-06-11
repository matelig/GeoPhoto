package com.polsl.android.geophotoapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.adapter.FilterItemAdapter
import com.polsl.android.geophotoapp.model.DateFilterType
import com.polsl.android.geophotoapp.model.PhotoFilter
import com.polsl.android.geophotoapp.model.SelectableFilterModel
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : BaseActivity() {
    private var exposures: ArrayList<String>? = null
    private var apertures: ArrayList<String>? = null
    private var devices: ArrayList<String>? = null
    private var focalLengths: ArrayList<String>? = null
    private var authors: ArrayList<String>? = null
    private var photoFilter: PhotoFilter = PhotoFilter()

    private lateinit var aperturesAdapter: FilterItemAdapter
    private lateinit var devicesAdapter: FilterItemAdapter
    private lateinit var focalLengthAdapter: FilterItemAdapter
    private lateinit var exposuresAdapter: FilterItemAdapter
    private lateinit var authorsAdapter: FilterItemAdapter

    companion object {
        const val FILTER_KEY = "filterKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setUpFiltersData()
        setUpDateRadioButtons()
        setUpButtons()
        setUpInitialSelections()
    }

    private fun setUpInitialSelections() {
        intent.getSerializableExtra(PhotoFilter.PHOTO_FILTER)?.let {
            photoFilter = it as PhotoFilter
            setDateSelection()
            setExposures()
            setFocalLengths()
            setAuthors()
            setDevices()
            setApertures()
        }
    }

    private fun setApertures() {
        for (aperture in photoFilter.apertures)
            aperturesAdapter.selectItem(aperture)
    }

    private fun setDevices() {
        for (device in photoFilter.devices)
            devicesAdapter.selectItem(device)
    }

    private fun setAuthors() {
        for (author in photoFilter.authors)
            authorsAdapter.selectItem(author)
    }

    private fun setFocalLengths() {
        for (focalLength in photoFilter.focalLengths)
            focalLengthAdapter.selectItem(focalLength)
    }

    private fun setExposures() {
        for (exposure in photoFilter.exposures)
            exposuresAdapter.selectItem(exposure)
    }

    private fun setDateSelection() {
        if (photoFilter.dateType == DateFilterType.ASCENDING)
            dateAscendingRb.isChecked = true
        else if (photoFilter.dateType == DateFilterType.DESCENDING)
            dateDescendingRb.isChecked = true
    }

    fun setUpButtons() {
        setUpResetButton()
        setUpApplyButton()
    }

    private fun setUpApplyButton() {
        applyFiltersBtn.setOnClickListener({ onApplyButtonClicked() })
    }

    private fun onApplyButtonClicked() {
        val resultIntent = Intent()
        resultIntent.putExtra(FILTER_KEY, photoFilter)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun setUpResetButton() {
        resetFiltersBtn.setOnClickListener({ onResetButtonClicked() })
    }

    private fun onResetButtonClicked() {
        photoFilter.resetFilter()
        dateAscendingRb.isChecked = false
        dateDescendingRb.isChecked = false
        aperturesAdapter.unselectAllItems()
        devicesAdapter.unselectAllItems()
        exposuresAdapter.unselectAllItems()
        focalLengthAdapter.unselectAllItems()
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
        authors = intent.getStringArrayListExtra(PhotoFilter.AUTHORS)
        setUpFilterAdapters()
    }

    private fun setUpFilterAdapters() {
        setUpAperturesAdapter()
        setUpDevicesAdapter()
        setUpExposuresAdapter()
        setUpFocalLengthsAdapter()
        setUpAuthorsAdapter()
    }

    private fun setUpAuthorsAdapter() {
        authorsAdapter = FilterItemAdapter(this)
        filterAuthorsRv.layoutManager = GridLayoutManager(this, 4)
        authorsAdapter.items = getSelectableFilterModels(authors) as ArrayList<Any>
        authorsAdapter.getItemClickObservable().subscribe({ t ->
            t as SelectableFilterModel
            if (t.isSelected)
                photoFilter.authors.add(t.value)
            else
                photoFilter.authors.remove(t.value)
        })
        filterAuthorsRv.adapter = authorsAdapter
    }

    private fun setUpFocalLengthsAdapter() {
        focalLengthAdapter = FilterItemAdapter(this)
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
        exposuresAdapter = FilterItemAdapter(this)
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
        devicesAdapter = FilterItemAdapter(this)
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
        aperturesAdapter = FilterItemAdapter(this)
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

