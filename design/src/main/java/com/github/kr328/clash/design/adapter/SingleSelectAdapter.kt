package com.github.kr328.clash.design.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

abstract class SingleSelectAdapter<T, VH : RecyclerView.ViewHolder>(
    private val itemClazz: Class<T>
) : RecyclerView.Adapter<VH>() {
    var itemList: List<T> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    fun getSelectedItem(): T? {
        if (selectedPosition in itemList.indices) {
            return itemList[selectedPosition]
        }
        return null
    }

    interface SingleSelectListener<T> {
        fun onSelectItemChange(oldItem: T?, newItem: T)
    }

    fun selectByObj(item: T) {
        setSelectedPosition(itemList.indexOf(item))
    }

    fun selectByIndex(index: Int) {
        if (index in itemList.indices) {
            setSelectedPosition(index)
        } else {
//            throw IllegalArgumentException()
        }
    }

    private var listener: SingleSelectListener<T>? = null

    fun setSingleSelectListener(listener: SingleSelectListener<T>) {
        this.listener = listener
    }

    fun setSelectedPosition(position: Int) {
        if (position != selectedPosition) {
            val oldItem =
                if (selectedPosition in itemList.indices) itemList[selectedPosition] else null
            val newItem = itemList.get(position)

            notifyItemChanged(selectedPosition)
            notifyItemChanged(position)

            listener!!.onSelectItemChange(oldItem, newItem)
            selectedPosition = position;
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        bindViewHolder(holder, position, itemList.get(position))
        if (position == selectedPosition) {
            bindViewHolderOnSelected(holder, position, itemList.get(position))
        } else {
            bindViewHolderOnUnSelected(holder, position, itemList.get(position))
        }
        holder.itemView
    }

    abstract fun bindViewHolder(holder: VH, position: Int, item: T)
    abstract fun bindViewHolderOnSelected(holder: VH, position: Int, item: T)
    abstract fun bindViewHolderOnUnSelected(holder: VH, position: Int, item: T)

    override fun getItemCount(): Int {
        return itemList.size
    }
}