package com.github.kr328.clash

import android.content.Intent
import com.github.kr328.clash.design.SubscriptionsDesign
import com.github.kr328.clash.service.model.PurchasePlan
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import com.google.gson.Gson
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

/**
 * 套餐选择界面
 */
class SubscriptionsActivity : BaseActivity<SubscriptionsDesign>() {

    override suspend fun main() {
        val design = SubscriptionsDesign(this)

        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                design.requests.onReceive {
                    when (it) {
                        is SubscriptionsDesign.Request.OnConfirm -> {
                            toOrderConfirmPage(it.subscription, it.sku)
                        }
                    }
                }
            }
        }
    }

    /**
     * 前往订单确认界面
     */
    private fun toOrderConfirmPage(
        subscription: Subscription,
        sku: Sku
    ) {
        val intent = Intent(this, OrderConfirmActivity::class.java)
        val gson = Gson()
        intent.putExtra(OrderConfirmActivity.EXTRA_SUBSCRIPTION, gson.toJson(subscription))
        intent.putExtra(OrderConfirmActivity.EXTRA_SUBSCRIPTION, gson.toJson(sku))
        startActivity(intent)
    }
}