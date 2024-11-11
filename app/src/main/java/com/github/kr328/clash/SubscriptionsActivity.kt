package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.design.SettingsDesign
import com.github.kr328.clash.design.SubscriptionsDesign
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.Sku
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import java.util.concurrent.TimeUnit


class SubscriptionsActivity : BaseActivity<SubscriptionsDesign>() {

    override suspend fun main() {
        val design = SubscriptionsDesign(this)

        setContentDesign(design)

        val ticker = ticker(TimeUnit.SECONDS.toMillis(1))

        while (isActive) {
            select<Unit> {
                events.onReceive {

                }
                design.requests.onReceive {
                    when (it) {
                        is SubscriptionsDesign.Request.SkipToSkuLink -> {
                            toWaitPaymentPage(it.paymentPlatform, it.sku)
                        }
                    }
                }
            }
        }
    }

    private fun toWaitPaymentPage(paymentPlatform: PaymentPlatform, sku: Sku) {
        val intent = Intent(this, WaitPaymentActivity::class.java)
        intent.putExtra(WaitPaymentActivity.EXTRA_PACKAGE_NAME, paymentPlatform.packageName)
        intent.putExtra(WaitPaymentActivity.EXTRA_SKU, sku)
        startActivity(intent)
    }
}