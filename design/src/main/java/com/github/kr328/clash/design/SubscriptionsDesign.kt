package com.github.kr328.clash.design

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.adapter.SingleSelectAdapter
import com.github.kr328.clash.design.adapter.SubscriptionAdapter
import com.github.kr328.clash.design.api.ApiResponse
import com.github.kr328.clash.design.api.BaseApiService
import com.github.kr328.clash.design.api.SubscriptionService
import com.github.kr328.clash.design.databinding.DesignSubscriptionsBinding
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.applyLinearAdapter
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.PurchasePlan
import com.github.kr328.clash.service.model.Subscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HttpsURLConnection

class SubscriptionsDesign(context: Context) : Design<SubscriptionsDesign.Request>(context),
    SingleSelectAdapter.SingleSelectListener<Subscription> {
    sealed class Request {
        /**
         * 买哪个订阅套餐，去哪个平台购买，对应平台上哪个 sku
         */
        data class OnOrderConfirm(val purchasePlan: PurchasePlan) : Request()
    }

    private val binding = DesignSubscriptionsBinding
        .inflate(context.layoutInflater, context.root, false)
    private val adapter = SubscriptionAdapter(context)
//    private inline val subscriptionService: SubscriptionService = BaseApiService.cre
    override val root: View
        get() = binding.root

    init {
        binding.self = this

        binding.activityBarLayout.applyFrom(context)

        binding.mainList.recyclerList.also {
            it.bindAppBarElevation(binding.activityBarLayout)
            it.applyLinearAdapter(context, adapter)
        }

        adapter.setSingleSelectListener(this)

        sendRequest()
    }

    private fun sendRequest() {
        // 显示 loading
        setLoading(true)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val subscriptionList = requestSubscriptionList()
                if (subscriptionList == null) {
                    showErrorDialog("请求套餐数据异常，请截图联系群主")
                    return@launch
                }
                if (subscriptionList != null) {
                    adapter.itemList = subscriptionList
                    adapter.selectByIndex(0)
                }
            } catch (e: Exception) {
                showErrorDialog("请求套餐数据异常，请截图联系群主")
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * 请求套餐数据
     */
    private suspend fun requestSubscriptionList(): List<Subscription>? {
        return withContext(Dispatchers.IO) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.100.214:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val apiService = retrofit.create(SubscriptionService::class.java)
                val response = apiService.findAll()
                if (response.isSuccess()) {
                    return@withContext response.data
                }
            } catch (e: Exception) {
            }
            return@withContext null
        }
    }

    private fun onPaymentPlatformConfirm(
        dialog: PaymentPlatformDialog,
        paymentPlatform: PaymentPlatform
    ) {
        dialog.dismiss()
        val selectedSubscription = adapter.getSelectedItem()
        if (selectedSubscription != null) {
            val sku = paymentPlatform.skuList[0]
            if (sku != null && !TextUtils.isEmpty(sku.link)) {
                requests.trySend(
                    Request.OnOrderConfirm(
                        PurchasePlan(
                            selectedSubscription,
                            paymentPlatform,
                            sku
                        )
                    )
                )
            }
        }
    }

    /**
     * 展示付款平台选择弹窗
     */
    fun showPaymentPlatformDialog() {
        val dialog = PaymentPlatformDialog(context, this::onPaymentPlatformConfirm)
        dialog.show()
    }

    /**
     * 当选中套餐时更新按钮价格
     */
    override fun onSelectItemChange(oldItem: Subscription?, newItem: Subscription) {
        binding.btnConfirm.text = String.format("确认（%.2f元）", newItem.price)
    }
}