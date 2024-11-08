package com.github.kr328.clash.design

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.github.kr328.clash.design.adapter.PaymentPlatformAdapter
import com.github.kr328.clash.design.adapter.SubscriptionAdapter
import com.github.kr328.clash.design.databinding.DesignSubscriptionsBinding
import com.github.kr328.clash.design.databinding.DialogPaymentPlatformBinding
import com.github.kr328.clash.design.databinding.DialogPlatformMenuBinding
import com.github.kr328.clash.design.dialog.AppBottomSheetDialog
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.applyLinearAdapter
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.patchDataSet
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentPlatformDialog(
    context: Context,
    onItemClick: (paymentPlatform: PaymentPlatform) -> Unit
) : BottomSheetDialog(context) {

    private val binding = DialogPaymentPlatformBinding
        .inflate(context.layoutInflater, context.root, false)
    private val adapter = PaymentPlatformAdapter(context, onItemClick)

    private val skuList: List<Sku> = listOf(
        Sku(1, 1, 1, ""),
        Sku(1, 1, 1, ""),
        Sku(1, 1, 1, ""),
    )
    private val _platformList: List<PaymentPlatform> = listOf(
        PaymentPlatform(
            1,
            "淘宝/天猫",
            "https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/taobao.png",
            skuList
        ),
        PaymentPlatform(
            2,
            "拼多多",
            "https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/pdd.png",
            skuList
        ),
    )

    init {
        setContentView(binding.root)
        binding.self = this

        binding.mainList.recyclerList.also {
            it.applyLinearAdapter(context, adapter)
        }

        CoroutineScope(Dispatchers.Main).launch {
            adapter.apply {
                patchDataSet(this::platformList, _platformList, id = { it.id })
            }
        }
    }
}