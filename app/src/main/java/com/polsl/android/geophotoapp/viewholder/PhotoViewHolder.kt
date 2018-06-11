package com.polsl.android.geophotoapp.viewholder

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.photo_item.view.*

class PhotoViewHolder(itemView: View?) : BaseViewHolder(itemView) {
    val photoIv = itemView?.photoIv
    val photoCb = itemView?.photoCb
    override var itemLayout = itemView?.itemLayout as ViewGroup?
}