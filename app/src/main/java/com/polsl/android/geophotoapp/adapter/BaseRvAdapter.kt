package com.polsl.android.geophotoapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Toast
import com.polsl.android.geophotoapp.viewholder.BaseViewHolder
import io.reactivex.subjects.PublishSubject

/**
 * Created by alachman on 29.04.2018.
 */
abstract class BaseRvAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    var items: ArrayList<Any>? = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val itemClickObservable = PublishSubject.create<Any>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        return onCreateBaseViewHolder(parent)
    }

    abstract fun onCreateBaseViewHolder(parent: ViewGroup?): BaseViewHolder

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: BaseViewHolder?, position: Int) {
        val item = items?.getOrNull(position)
        holder?.itemLayout?.setOnClickListener {
            if (item != null) {
                itemClickObservable.onNext(item)
            }
        }
        onBindHolder(holder, item)
    }

    abstract fun onBindHolder(holder: BaseViewHolder?, item: Any?)

    fun getItemClickObservable() = itemClickObservable
}