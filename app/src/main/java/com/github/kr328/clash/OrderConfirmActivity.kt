package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import android.os.Handler
import com.github.kr328.clash.design.OrderConfirmDesign
import com.github.kr328.clash.design.dialog.DialogManager
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

    // 索要购买的套餐
    private lateinit var subscription: Subscription

    // 对应的 sku
    private lateinit var sku: Sku

    private val handler = Handler()
    override suspend fun main() {
        val design = OrderConfirmDesign(this)
        setContentDesign(design)
        initData()

        while (isActive) {
            select<Unit> {
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


    private fun initData() {
        val gson = Gson()
        subscription =
            gson.fromJson(intent.getStringExtra(EXTRA_SUBSCRIPTION), Subscription::class.java)
        sku = gson.fromJson(intent.getStringExtra(EXTRA_SKU), Sku::class.java)
        design?.binding?.subscription = subscription
        design?.binding?.platform = sku.platform
    }

    private fun openPaymentPlatformApp() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(sku.link))
            intent.setPackage(sku.platform.packageName)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            DialogManager.showLoadingDialog("前往付款平台中...")
            handler.postDelayed({ DialogManager.hideLoadingDialog() }, 1000 * 10)
        } catch (e: Exception) {
            DialogManager.hideLoadingDialog()
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

    override fun onStop() {
        super.onStop()
        design!!.binding.btnConfirm.alpha = 0.5f
        design!!.binding.platformAppOpened = true
        DialogManager.hideLoadingDialog()
    }

    private fun skipToCodeInputPage() {
        val intent = Intent(this, ActivationCodeInputActivity::class.java)
        startActivity(intent)
    }
}