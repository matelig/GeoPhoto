package com.polsl.android.geophotoapp.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by alachman on 29.04.2018.
 */
abstract class BaseViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
    abstract var itemLayout: ViewGroup?
}