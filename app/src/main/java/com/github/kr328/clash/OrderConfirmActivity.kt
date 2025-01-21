package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import com.github.kr328.clash.design.OrderConfirmDesign
import com.github.kr328.clash.service.model.PurchasePlan
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

/**
 * 订单确认界面
 */
class OrderConfirmActivity : BaseActivity<OrderConfirmDesign>() {
    companion object {
        val EXTRA_SUBSCRIPTION = "EXTRA_SUBSCRIPTION"
        val EXTRA_SKU = "EXTRA_SKU"
    }

    private lateinit var subscription: Subscription
    private lateinit var sku: Sku

    override suspend fun main() {
        val design = OrderConfirmDesign(this)
        setContentDesign(design)

        val gson = Gson()
        subscription =
            gson.fromJson(intent.getStringExtra(EXTRA_SUBSCRIPTION), Subscription::class.java)
        sku = gson.fromJson(intent.getStringExtra(EXTRA_SKU), Sku::class.java)
        design.binding.subscription = subscription
        design.binding.platform = sku.platform
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
                            skipToCodeInputPage()
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
            intent.setPackage(sku.platform.packageName)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.design?.binding?.platformAppOpened = true
        } catch (e: Exception) {
            val platformName = sku.platform.name
            MaterialAlertDialogBuilder(this)
                .setTitle(String.format("您的设备未安装%sAPP", platformName))
                .setMessage(
                    String.format(
                        "1. 请安装%sAPP后再付款\n2. 或者选择其他付款平台",
                        platformName
                    )
                )
                .setPositiveButton("确认") { _, _ -> finish() }
                .show()
        }
    }

    fun skipToCodeInputPage() {
        val intent = Intent(this, ActivationCodeInputActivity::class.java)
        startActivity(intent)
    }
}