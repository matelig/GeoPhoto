package com.polsl.android.geophotoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.model.SelectableFilterModel
import com.polsl.android.geophotoapp.viewholder.BaseViewHolder
import com.polsl.android.geophotoapp.viewholder.FilterViewHolder

class FilterItemAdapter(private val context: Context) : BaseRvAdapter() {
    override fun onCreateBaseViewHolder(parent: ViewGroup?): BaseViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.filter_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindHolder(holder: BaseViewHolder?, item: Any?) {

        (holder as FilterViewHolder).filterTv?.text = (item as SelectableFilterModel).value
        holder.itemLayout?.setOnClickListener({
            item.isSelected = !item.isSelected
            notifyDataSetChanged()
            getItemClickObservable().onNext(item)
        })
        if (item.isSelected)
            holder.itemLayout?.background = context.getDrawable(R.drawable.pink_round_background)
        else
            holder.itemLayout?.background = context.getDrawable(R.drawable.white_round_background)
    }

     fun unselectAllItems() {
        for (item in items as ArrayList<SelectableFilterModel>)
            item.isSelected = false
         notifyDataSetChanged()
    }

    fun selectItem(value: String) {
        (items as ArrayList<SelectableFilterModel>).find { it.value == value }?.isSelected = true
        notifyDataSetChanged()
    }

}