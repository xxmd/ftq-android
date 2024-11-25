package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.design.SettingsDesign
import com.github.kr328.clash.design.SubscriptionsDesign
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

/**
 * 订阅套餐选择界面
 */
class SubscriptionsActivity : BaseActivity<SubscriptionsDesign>() {

    override suspend fun main() {
        val design = SubscriptionsDesign(this)

        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                events.onReceive {

                }
                design.requests.onReceive {
                    when (it) {
                        is SubscriptionsDesign.Request.onOrderConfirm -> {
                            toOrderConfirmPage(it.subscription, it.paymentPlatform, it.sku)
                        }
                    }
                }
            }
        }
    }

    /**
     * 前往订单确认界面
     */
    private fun toOrderConfirmPage(subscription: Subscription, paymentPlatform: PaymentPlatform, sku: Sku) {
        val intent = Intent(this, OrderConfirmActivity::class.java)
        intent.putExtra(OrderConfirmActivity.EXTRA_SUBSCRIPTION, Json.encodeToString(subscription))
        intent.putExtra(OrderConfirmActivity.EXTRA_PAYMENT_PLATFORM, Json.encodeToString(paymentPlatform))
        intent.putExtra(OrderConfirmActivity.EXTRA_SKU, Json.encodeToString(sku))
        startActivity(intent)
    }
}