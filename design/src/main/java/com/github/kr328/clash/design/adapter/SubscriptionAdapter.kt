package com.github.kr328.clash.design.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.databinding.AdapterProfileBinding
import com.github.kr328.clash.design.databinding.AdapterSubscriptionBinding
import com.github.kr328.clash.design.model.ProfilePageState
import com.github.kr328.clash.design.model.ProxyPageState
import com.github.kr328.clash.design.ui.ObservableCurrentTime
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.service.model.Subscription

class SubscriptionAdapter(
    private val context: Context,
) : SingleSelectAdapter<Subscription, SubscriptionAdapter.Holder>(Subscription::class.java) {
    class Holder(val binding: AdapterSubscriptionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun bindViewHolder(holder: Holder, position: Int, item: Subscription) {
        val current = itemList[position]
        holder.binding.subscription = current
        holder.itemView.setOnClickListener {
            selectByIndex(position)
        }
    }

    override fun bindViewHolderOnSelected(holder: Holder, position: Int, item: Subscription) {
        holder.binding.iconView.isChecked = true
    }

    override fun bindViewHolderOnUnSelected(holder: Holder, position: Int, item: Subscription) {
        holder.binding.iconView.isChecked = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            AdapterSubscriptionBinding
                .inflate(context.layoutInflater, parent, false)
        )
    }
}