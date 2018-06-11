package com.polsl.android.geophotoapp.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

abstract class BaseViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
    abstract var itemLayout: ViewGroup?
}