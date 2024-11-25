package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.design.SettingsDesign
import com.github.kr328.clash.design.SubscriptionsDesign
import com.github.kr328.clash.design.WaitPaymentDesign
import com.github.kr328.clash.service.model.Sku
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import java.util.concurrent.TimeUnit


class WaitPaymentActivity : BaseActivity<WaitPaymentDesign>() {
    companion object {
        val EXTRA_SUBSCRIPTION = "EXTRA_SUBSCRIPTION"
        val EXTRA_PAYMENT_PLATFORM = "EXTRA_PAYMENT_PLATFORM"
        val EXTRA_SKU = "EXTRA_SKU"
    }

    override suspend fun main() {
        val design = WaitPaymentDesign(this)

        setContentDesign(design)

//        if (packageName != null && sku != null) {
//            skipBySkuLink(packageName, sku.link)
//        }
        while (isActive) {
            select<Unit> {
                events.onReceive {

                }
                design.requests.onReceive {
//                    when (it) {
//                    }
                }
            }
        }
    }

    private suspend fun SubscriptionsDesign.fetch() {

    }


    private fun skipBySkuLink(packageName: String, link: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setPackage(packageName)
        intent.setData(Uri.parse(link))
        startActivity(intent)
    }
}