package com.github.kr328.clash.design

import android.content.Context
import android.view.View
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.adapter.SingleSelectAdapter
import com.github.kr328.clash.design.adapter.SubscriptionAdapter
import com.github.kr328.clash.design.databinding.DesignSubscriptionsBinding
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.applyLinearAdapter
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.patchDataSet
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.service.model.Subscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SubscriptionsDesign(context: Context) : Design<SubscriptionsDesign.Request>(context) {
    sealed class Request {
        object UpdateAll : Request()
        object Create : Request()
        data class Active(val profile: Profile) : Request()
        data class Update(val profile: Profile) : Request()
        data class Edit(val profile: Profile) : Request()
        data class Duplicate(val profile: Profile) : Request()
        data class Delete(val profile: Profile) : Request()
    }

    private val binding = DesignSubscriptionsBinding
        .inflate(context.layoutInflater, context.root, false)
    private val adapter = SubscriptionAdapter(context)


    private fun onSelectItemChange(subscription: Subscription) {
        Log.i("onSelectItemChange: " + subscription)
    }

    val _subscriptionList: List<Subscription> = listOf(
        Subscription(1, "包天套餐", 1.00, 1, true),
        Subscription(2, "包月套餐", 10.00, 30, false),
        Subscription(3, "包年套餐", 50.00, 365, false),
    )
    override val root: View
        get() = binding.root

    init {
        binding.self = this

        binding.activityBarLayout.applyFrom(context)

        binding.mainList.recyclerList.also {
            it.bindAppBarElevation(binding.activityBarLayout)
            it.applyLinearAdapter(context, adapter)
        }

        adapter.setSingleSelectListener(object : SingleSelectAdapter.SingleSelectListener<Subscription> {
            override fun onSelectItemChange(oldItem: Subscription, newItem: Subscription) {
                onSelectItemChange(newItem)
            }
        })


        adapter.itemList = _subscriptionList

//        CoroutineScope(Dispatchers.Main).launch {
//            adapter.apply {
//                patchDataSet(this::subscriptionList, _subscriptionList, id = { it.id })
//            }
//        }
    }
}