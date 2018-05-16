package com.polsl.android.geophotoapp.viewholder

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.filter_item.view.*
/**
 * Created by alachman on 06.05.2018.
 */
class FilterViewHolder(itemView: View?) : BaseViewHolder(itemView) {
    val filterTv = itemView?.filterValueTv
    override var itemLayout = itemView?.itemLayout as ViewGroup?
}