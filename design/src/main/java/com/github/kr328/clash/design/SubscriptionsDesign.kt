package com.github.kr328.clash.design

import android.content.Context
import android.view.View
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.adapter.SingleSelectAdapter
import com.github.kr328.clash.design.adapter.SubscriptionAdapter
import com.github.kr328.clash.design.api.ApiClient
import com.github.kr328.clash.design.databinding.DesignSubscriptionsBinding
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.applyLinearAdapter
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.Platform
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 订阅套餐选择页面
 */
class SubscriptionsDesign(context: Context) : Design<SubscriptionsDesign.Request>(context),
    SingleSelectAdapter.SingleSelectListener<Subscription> {
    private val binding =
        DesignSubscriptionsBinding.inflate(context.layoutInflater, context.root, false)
    private val adapter = SubscriptionAdapter(context)
    override val root: View
        get() = binding.root

    sealed class Request {
        data class OnConfirm(val subscription: Subscription, val sku: Sku) : Request()
    }

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
            val subscriptionList = requestSubscriptionList()
            if (subscriptionList != null) {
                adapter.itemList = subscriptionList
                adapter.selectByIndex(0)
            }
        }
    }

    /**
     * 请求套餐列表
     */
    private suspend fun requestSubscriptionList(): List<Subscription>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.subscriptionService.findAll()
                if (response.isSuccess()) {
                    return@withContext response.data
                }
            } catch (e: Exception) {
                // TODO 告知用户获取套餐列表失败
                Log.e(e.message!!, e)
            }
            return@withContext null
        }
    }

    private fun onPaymentPlatformConfirm(
        dialog: PlatformDialog,
        platform: Platform
    ) {
        dialog.dismiss()
        val selectedSubscription = adapter.getSelectedItem()
        if (selectedSubscription != null) {
//            val sku = paymentPlatform.skuList[0]
//            if (sku != null && !TextUtils.isEmpty(sku.link)) {
//                requests.trySend(
//                    Request.OnOrderConfirm(
//                        PurchasePlan(
//                            selectedSubscription,
//                            paymentPlatform,
//                            sku
//                        )
//                    )
//                )
//            }
        }
    }

    /**
     * 展示付款平台选择弹窗
     */
    fun showPaymentPlatformDialog() {
        setLoading(true)


        GlobalScope.launch(Dispatchers.Main) {
            try {
                val selectedSubscription = adapter.getSelectedItem()
                if (selectedSubscription != null) {
                    val platformList = requestPaymentPlatformList(selectedSubscription.id)
                    if (platformList != null) {
                        val dialog = PlatformDialog(context, platformList) { dialog, platform ->
                            onPaymentPlatformConfirm(dialog, platform)
                        }
                        dialog.show()
                    }
                }
            } catch (e: Exception) {
                showErrorDialog("请求套餐数据异常，请截图联系群主")
            } finally {
                setLoading(false)
            }
        }
    }


    /**
     * 请求套餐列表
     */
    private suspend fun requestPaymentPlatformList(subscriptionId: Long): List<Platform>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.skuService.findAllBySubscriptionId(subscriptionId)
                Log.i(response.data.toString())
                if (response.isSuccess() && response.data != null) {
                    return@withContext convertSkuList(response.data)
                }
            } catch (e: Exception) {
                Log.e(e.message!!, e)
            }
            return@withContext null
        }
    }

    private fun convertSkuList(skuList: List<Sku>): List<Platform> {
        val platformList = ArrayList<Platform>()
        if (skuList != null) {
            for (sku in skuList) {
                platformList.add(sku.platform)
            }
        }
        return platformList
    }

    /**
     * 当选中套餐时更新按钮价格
     */
    override fun onSelectItemChange(oldItem: Subscription?, newItem: Subscription) {
        binding.btnConfirm.text = String.format("确认（%.2f元）", newItem.price)
    }
}