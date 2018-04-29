package com.polsl.android.geophotoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.model.SelectablePhotoModel
import com.polsl.android.geophotoapp.viewholder.BaseViewHolder
import com.polsl.android.geophotoapp.viewholder.PhotoViewHolder
import com.squareup.picasso.Picasso
import io.reactivex.subjects.PublishSubject

/**
 * Created by alachman on 29.04.2018.
 */

class ImageRvAdapter(private val context: Context) : BaseRvAdapter() {
    val selectedItemsObservable = PublishSubject.create<Int>()
    private var isSelecting = false
    override fun onCreateBaseViewHolder(parent: ViewGroup?): BaseViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindHolder(holder: BaseViewHolder?, item: Any?) {

        Picasso.get().load((item as SelectablePhotoModel).photo.url)
                .placeholder(R.drawable.no_photo)
                .resize(context.resources.getDimension(R.dimen.photo_thumbnail_size).toInt(),
                        context.resources.getDimension(R.dimen.photo_thumbnail_size).toInt())
                .centerCrop()
                .into((holder as PhotoViewHolder).photoIv)
        holder.photoCb?.isChecked = item.isSelected
        holder.photoCb?.setOnClickListener({
            holder.photoCb?.isChecked = !item.isSelected
            item.isSelected = !item.isSelected
            updateSelectedItemsObservable()
        }
        )
        if (isSelecting)
            holder.photoCb?.visibility = View.VISIBLE
        else
            holder.photoCb?.visibility = View.GONE
        (holder.itemLayout?.setOnLongClickListener(View.OnLongClickListener {
            isSelecting = !isSelecting
            if(!isSelecting)
                unselectAllItems()
            notifyDataSetChanged()
            return@OnLongClickListener true
        }))
    }

    private fun unselectAllItems() {
        for(item in items as ArrayList<SelectablePhotoModel>)
            item.isSelected = false
        selectedItemsObservable.onNext(0)
    }

    private fun updateSelectedItemsObservable() {
        selectedItemsObservable.onNext(getSelectedItemsCount())
    }

    private fun getSelectedItemsCount(): Int =
            (items as ArrayList<SelectablePhotoModel>).filter { selectablePhotoModel -> selectablePhotoModel.isSelected }.count()

}