package com.github.kr328.clash

import android.content.Intent
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.SubscriptionsDesign
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.PurchasePlan
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import com.google.gson.Gson
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

/**
 * 订阅套餐选择界面
 */
class SubscriptionsActivity : BaseActivity<SubscriptionsDesign>() {

    override suspend fun main() {
        val design = SubscriptionsDesign(this)

        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                design.requests.onReceive {
                    when (it) {
                        is SubscriptionsDesign.Request.OnOrderConfirm -> {
                            toOrderConfirmPage(it.purchasePlan)
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
        purchasePlan: PurchasePlan
    ) {
        val intent = Intent(this, OrderConfirmActivity::class.java)
        val gson = Gson()
        intent.putExtra(OrderConfirmActivity.EXTRA_PURCHASE_PLAN, gson.toJson(purchasePlan))
        startActivity(intent)
    }
}