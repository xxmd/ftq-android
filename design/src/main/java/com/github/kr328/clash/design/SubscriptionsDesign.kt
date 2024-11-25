package com.github.kr328.clash.design

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.ProxyDesign.Request
import com.github.kr328.clash.design.adapter.SingleSelectAdapter
import com.github.kr328.clash.design.adapter.SubscriptionAdapter
import com.github.kr328.clash.design.databinding.DesignSubscriptionsBinding
import com.github.kr328.clash.design.databinding.DialogPlatformMenuBinding
import com.github.kr328.clash.design.databinding.DialogProfilesMenuBinding
import com.github.kr328.clash.design.dialog.AppBottomSheetDialog
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.applyLinearAdapter
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.patchDataSet
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SubscriptionsDesign(context: Context) : Design<SubscriptionsDesign.Request>(context),
    SingleSelectAdapter.SingleSelectListener<Subscription> {

    sealed class Request {
        /**
         * 买哪个订阅套餐，去哪个平台购买，对应平台上哪个 sku
         */
        data class onOrderConfirm(
            val subscription: Subscription,
            val paymentPlatform: PaymentPlatform,
            val sku: Sku
        ) : Request()
    }

    private val binding = DesignSubscriptionsBinding
        .inflate(context.layoutInflater, context.root, false)
    private val adapter = SubscriptionAdapter(context)

    private val subscriptionList: List<Subscription> = listOf(
        Subscription(1, "包天套餐", 1.00, 1),
        Subscription(2, "包月套餐", 10.00, 30),
        Subscription(3, "包年套餐", 50.00, 365),
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

        adapter.setSingleSelectListener(this)
        adapter.itemList = subscriptionList
        adapter.selectByIndex(0)

//        CoroutineScope(Dispatchers.Main).launch {
//            adapter.apply {
//                patchDataSet(this::subscriptionList, _subscriptionList, id = { it.id })
//            }
//        }
    }

    private fun onPaymentPlatformConfirm(
        dialog: PaymentPlatformDialog,
        paymentPlatform: PaymentPlatform
    ) {
        Log.i("dialog.dismiss()")
        dialog.dismiss()
        val selectedSubscription = adapter.getSelectedItem()
        if (selectedSubscription != null) {
            val sku = paymentPlatform.skuList.find { it.subscriptionId == selectedSubscription.id }
            if (sku != null && !TextUtils.isEmpty(sku.link)) {
                Log.i("trySend onOrderConfirm")
                requests.trySend(Request.onOrderConfirm(selectedSubscription, paymentPlatform, sku))
            }
        }
    }

    fun showPaymentPlatformDialog() {
        val dialog = PaymentPlatformDialog(context, this::onPaymentPlatformConfirm)
        dialog.show()
    }

    override fun onSelectItemChange(oldItem: Subscription?, newItem: Subscription) {
        binding.btnConfirm.text = String.format("确认（%.2f元）", newItem.price)
    }
}