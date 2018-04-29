package com.polsl.android.geophotoapp.viewholder

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.photo_item.view.*

/**
 * Created by alachman on 29.04.2018.
 */
class PhotoViewHolder(itemView: View?) : BaseViewHolder(itemView) {
    val photoIv = itemView?.photoIv
    val photoCb = itemView?.photoCb
    override var itemLayout = itemView?.itemLayout as ViewGroup?
}