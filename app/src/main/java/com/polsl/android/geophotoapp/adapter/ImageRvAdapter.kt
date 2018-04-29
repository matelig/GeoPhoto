package com.polsl.android.geophotoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.viewholder.BaseViewHolder
import com.polsl.android.geophotoapp.viewholder.PhotoViewHolder
import com.squareup.picasso.Picasso

/**
 * Created by alachman on 29.04.2018.
 */

class ImageRvAdapter(private val context: Context) : BaseRvAdapter() {
    override fun onCreateBaseViewHolder(parent: ViewGroup?): BaseViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindHolder(holder: BaseViewHolder?, item: Any?) {

        Picasso.get().load(item as String)
                .placeholder(R.drawable.no_photo)
                .resize(context.resources.getDimension(R.dimen.photo_thumbnail_size).toInt(),
                        context.resources.getDimension(R.dimen.photo_thumbnail_size).toInt())
                .centerCrop()
                .into((holder as PhotoViewHolder).photoIv)
    }
}