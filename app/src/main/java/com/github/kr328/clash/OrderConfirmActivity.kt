package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import com.github.kr328.clash.design.OrderConfirmDesign
import com.github.kr328.clash.service.model.PurchasePlan
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

/**
 * 订单确认界面
 */
class OrderConfirmActivity : BaseActivity<OrderConfirmDesign>() {
    companion object {
        val EXTRA_PURCHASE_PLAN = "EXTRA_PURCHASE_PLAN"
    }

    private lateinit var purchasePlan: PurchasePlan

    override suspend fun main() {
        val design = OrderConfirmDesign(this)
        setContentDesign(design)

        val gson = Gson()
        purchasePlan = gson.fromJson(intent.getStringExtra(EXTRA_PURCHASE_PLAN), PurchasePlan::class.java)
        design.binding.subscription = purchasePlan.subscription
        design.binding.paymentPlatform = purchasePlan.paymentPlatform
        design.binding.sku = purchasePlan.sku
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
            intent.setData(Uri.parse(purchasePlan.sku.link))
            intent.setPackage(purchasePlan.paymentPlatform.packageName)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.design?.binding?.platformAppOpened = true
        } catch (e: Exception) {
            MaterialAlertDialogBuilder(this)
                .setTitle(String.format("您的设备未安装%sAPP", purchasePlan.paymentPlatform.name))
                .setMessage(
                    String.format(
                        "1. 请安装%sAPP后再付款\n2. 或者选择其他付款平台",
                        purchasePlan.paymentPlatform.name
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