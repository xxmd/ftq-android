package com.github.kr328.clash.design

import android.content.Context
import com.github.kr328.clash.design.adapter.PaymentPlatformAdapter
import com.github.kr328.clash.design.databinding.DialogPaymentPlatformBinding
import com.github.kr328.clash.design.util.applyLinearAdapter
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.patchDataSet
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.Platform
import com.github.kr328.clash.service.model.Sku
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmOrderDialog(
    context: Context,
    onItemClick: (dialog: ConfirmOrderDialog, paymentPlatform: Platform) -> Unit
) : BottomSheetDialog(context) {

    private val binding = DialogPaymentPlatformBinding
        .inflate(context.layoutInflater, context.root, false)
    private val adapter = PaymentPlatformAdapter(context, { item ->
        onItemClick(this, item)
    })

//    private val taobaoSkuList: List<Sku> = listOf(
//        Sku(1, 1, 1, "http://e.tb.cn/h.TZz5gCx?tk=ahaG3JlVcIa"),
//        Sku(1, 1, 1, "http://e.tb.cn/h.TZz5gCx?tk=ahaG3JlVcIa"),
//        Sku(1, 1, 1, "http://e.tb.cn/h.TZz5gCx?tk=ahaG3JlVcIa"),
//    )
//    private val tmallSkuList: List<Sku> = listOf(
//        Sku(1, 1, 1, "https://m.tb.cn/h.TZz8ZL5?tk=gGks3Jl6ude"),
//        Sku(1, 1, 2, "https://m.tb.cn/h.TZz8ZL5?tk=gGks3Jl6ude"),
//        Sku(1, 1, 3, "https://m.tb.cn/h.TZz8ZL5?tk=gGks3Jl6ude"),
//    )
//    private val pinDuoDuoSkuList: List<Sku> = listOf(
//        Sku(1, 1, 1, "https://mobile.yangkeduo.com/goods2.html?ps=MhRjAdwDZQ"),
//        Sku(1, 1, 2, "https://mobile.yangkeduo.com/goods2.html?ps=MhRjAdwDZQ"),
//        Sku(1, 1, 3, "https://mobile.yangkeduo.com/goods2.html?ps=MhRjAdwDZQ"),
//    )
//    private val _platformList: List<Platform> = listOf(
//        Platform(
//            "淘宝",
//            "https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/taobao.png",
//            "com.taobao.taobao",
//            taobaoSkuList
//        ),
//        Platform(
//            "天猫",
//            "https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/tianmao.png",
//            "com.tmall.wireless",
//            taobaoSkuList
//        ),
//        Platform(
//            "拼多多",
//            "https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/pdd.png",
//            "com.xunmeng.pinduoduo",
//            pinDuoDuoSkuList
//        ),
//    )

    init {
        setContentView(binding.root)
        binding.self = this

        binding.mainList.recyclerList.also {
            it.applyLinearAdapter(context, adapter)
        }

        CoroutineScope(Dispatchers.Main).launch {
//            adapter.apply {
//                patchDataSet(this::platformList, _platformList, id = { it.id })
//            }
        }
    }
}