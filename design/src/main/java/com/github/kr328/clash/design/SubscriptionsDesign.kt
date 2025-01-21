package com.github.kr328.clash.design

import android.content.Context
import android.view.View
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

    lateinit var selectedsubscription: Subscription
    lateinit var skuList: List<Sku>
    lateinit var platformList: List<Platform>

    init {
        binding.self = this
        binding.activityBarLayout.applyFrom(context)
        binding.mainList.recyclerList.also {
            it.bindAppBarElevation(binding.activityBarLayout)
            it.applyLinearAdapter(context, adapter)
        }
        adapter.setSingleSelectListener(this)
        initData()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        showLoadingDialog()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val subscriptionList = requestSubscriptionList()
                if (subscriptionList != null) {
                    adapter.itemList = subscriptionList
                    adapter.selectByIndex(0)
                }
            } finally {
                closeLoadingDialog()
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
                showErrorDialog("获取套餐列表失败，请联系群主")
            }
            return@withContext null
        }
    }

    /**
     * 当付款平台选择完毕
     */
    private fun onPlatformConfirm(
        dialog: PlatformDialog,
        platform: Platform
    ) {
        dialog.dismiss()
        var index = platformList.indexOf(platform)
        skipToOrderConfirmPage(skuList.get(index))
    }

    /**
     * 跳转至订单确认页面
     */
    private fun skipToOrderConfirmPage(sku: Sku) {
        var subscription = adapter.getSelectedItem()
        if (subscription != null && sku != null) {
            requests.trySend(
                Request.OnConfirm(subscription, sku)
            )
        }
    }

    /**
     * 当确认按钮被点击
     */
    fun onConfirmBtnClick() {
        showLoadingDialog()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val selectedSubscription = adapter.getSelectedItem()
                if (selectedSubscription != null) {
                    val platformList = requestPlatformList(selectedSubscription.id)
                    onPlatformRequestSuccess(platformList)
                }
            } catch (e: Exception) {
                showErrorDialog("请求套餐数据异常，请截图联系群主")
            } finally {
                closeLoadingDialog()
            }
        }
    }

    /**
     * 平台列表请求成功
     */
    private fun onPlatformRequestSuccess(platformList: List<Platform>?) {
        if (platformList == null || platformList.size == 0) {
            return
        }
        this.platformList = platformList
        if (platformList.size == 1) {
            skipToOrderConfirmPage(skuList.get(0))
        } else {
            PlatformDialog(context, platformList) { dialog, platform ->
                onPlatformConfirm(dialog, platform)
            }.show()
        }
    }

    /**
     * 请求可用平台列表
     */
    private suspend fun requestPlatformList(subscriptionId: Long): List<Platform>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.skuService.findAllBySubscriptionId(subscriptionId)
                if (response.isSuccess() && response.data != null) {
                    skuList = response.data
                    return@withContext extractPlatformList(response.data)
                }
            } catch (e: Exception) {
                showErrorDialog("获取平台列表失败: " + e.message)
            }
            return@withContext null
        }
    }

    /**
     * 从 sku 列表中提前平台列表
     */
    private fun extractPlatformList(skuList: List<Sku>): List<Platform> {
        val platformList = ArrayList<Platform>()
        if (skuList != null) {
            for (sku in skuList) {
                platformList.add(sku.platform)
            }
        }
        return platformList
    }

    /**
     * 当选中套餐出现变更
     */
    override fun onSelectItemChange(oldItem: Subscription?, newItem: Subscription) {
        selectedsubscription = newItem
        binding.btnConfirm.text = String.format("确认（%.2f元）", newItem.price)
    }
}