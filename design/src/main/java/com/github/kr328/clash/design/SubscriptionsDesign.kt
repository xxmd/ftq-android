package com.github.kr328.clash.design

import android.content.Context
import android.view.View
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.adapter.SingleSelectAdapter
import com.github.kr328.clash.design.adapter.SubscriptionAdapter
import com.github.kr328.clash.design.api.base.ApiClient
import com.github.kr328.clash.design.api.base.SimpleCallback
import com.github.kr328.clash.design.api.SkuApi
import com.github.kr328.clash.design.api.SubscriptionApi
import com.github.kr328.clash.design.databinding.DesignSubscriptionsBinding
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.applyLinearAdapter
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.Platform
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription

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

    // 选中的套餐
    lateinit var selectedsubscription: Subscription

    // sku 列表
    lateinit var skuList: List<Sku>

    // 平台列表
    lateinit var platformList: List<Platform>

    // 订阅套餐接口
    val subscriptionApi = ApiClient.create<SubscriptionApi>()

    // sku 接口
    val skuApi = ApiClient.create<SkuApi>()

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
        val call = subscriptionApi.findAll()
        call.enqueue(SimpleCallback<List<Subscription>>(
            onSuccess = { data ->
                adapter.itemList = data!!
                adapter.selectByIndex(0)
            }
        ))
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
        Log.i("onConfirmBtnClick")
        val call = skuApi.findAllBySubscriptionId(selectedsubscription.id)
        call.enqueue(SimpleCallback<List<Sku>>(
            onSuccess = { data ->
                Log.i("onSuccess" + data)
                skuList = data!!
                platformList = extractPlatformList(skuList)
                onPlatformListConfirm()
            }
        ))
    }

    /**
     * 当平台列表确认
     */
    private fun onPlatformListConfirm() {
        if (platformList.size == 1) {
            skipToOrderConfirmPage(skuList.get(0))
        } else {
            PlatformDialog(context, platformList) { dialog, platform ->
                onPlatformConfirm(dialog, platform)
            }.show()
        }
    }

    /**
     * 从 sku 列表中提取平台列表
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