package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.github.kr328.clash.design.OrderConfirmDesign
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.dialog.requestModelTextInput
import com.github.kr328.clash.design.util.ValidatorUUIDString
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.serialization.json.Json

/**
 * 订单确认界面
 */
class OrderConfirmActivity : BaseActivity<OrderConfirmDesign>() {
    companion object {
        val EXTRA_SUBSCRIPTION = "EXTRA_SUBSCRIPTION"
        val EXTRA_PAYMENT_PLATFORM = "EXTRA_PAYMENT_PLATFORM"
        val EXTRA_SKU = "EXTRA_SKU"
    }

    private lateinit var subscription: Subscription
    private lateinit var paymentPlatform: PaymentPlatform
    private lateinit var sku: Sku

    override suspend fun main() {
        val design = OrderConfirmDesign(this)
        setContentDesign(design)

        subscription = intent.getStringExtra(EXTRA_SUBSCRIPTION)
            ?.let { Json.decodeFromString<Subscription>(it) }
            ?: throw IllegalStateException("Missing EXTRA_SUBSCRIPTION in intent")
        paymentPlatform = intent.getStringExtra(EXTRA_PAYMENT_PLATFORM)
            ?.let { Json.decodeFromString<PaymentPlatform>(it) }
            ?: throw IllegalStateException("Missing EXTRA_SUBSCRIPTION in intent")
        sku = intent.getStringExtra(EXTRA_SKU)?.let { Json.decodeFromString<Sku>(it) }
            ?: throw IllegalStateException("Missing EXTRA_SUBSCRIPTION in intent")

        design.binding.subscription = subscription
        design.binding.paymentPlatform = paymentPlatform
        design.binding.sku = sku
        design.binding.platformAppOpened = false

        while (isActive) {
            select<Unit> {
                events.onReceive {

                }
                design.requests.onReceive {
                    when (it) {
                        is OrderConfirmDesign.Request.OnConfirm -> {
                            openPaymentPlatformApp()
                        }

                        is OrderConfirmDesign.Request.ToActivationCodeInputPage -> {
                            toActivationCodeInputPage()
                        }
                    }
                }
            }
        }
    }

    private fun openPaymentPlatformApp() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(sku.link))
            intent.setPackage(paymentPlatform.packageName)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.design?.binding?.platformAppOpened = true
        } catch (e: Exception) {
            MaterialAlertDialogBuilder(this)
                .setTitle(String.format("您的设备未安装%sAPP", paymentPlatform.name))
                .setMessage(
                    String.format(
                        "1. 请安装%sAPP后再付款\n2. 或者选择其他付款平台",
                        paymentPlatform.name
                    )
                )
                .setPositiveButton("确认") { _, _ -> finish() }
                .show()
        }
    }

    fun toActivationCodeInputPage() {
        val intent = Intent(this, ActivationCodeInputActivity::class.java)
        startActivity(intent)
    }
}