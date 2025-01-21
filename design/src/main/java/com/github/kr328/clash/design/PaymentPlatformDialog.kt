package com.github.kr328.clash.design

import android.content.Context
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.adapter.PaymentPlatformAdapter
import com.github.kr328.clash.design.api.ApiClient
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
import kotlinx.coroutines.withContext
import java.util.ArrayList
import java.util.Collections
import java.util.stream.Collectors

class PlatformDialog(
    context: Context,
    platformList: List<Platform>,
    onItemClick: (dialog: PlatformDialog, platform: Platform) -> Unit
) : BottomSheetDialog(context) {

    private val binding =
        DialogPaymentPlatformBinding.inflate(context.layoutInflater, context.root, false)

    private val adapter = PaymentPlatformAdapter(context, { item ->
        onItemClick(this, item)
    })

    init {
        setContentView(binding.root)
        binding.self = this
        binding.mainList.recyclerList.also {
            it.applyLinearAdapter(context, adapter)
        }
        adapter.platformList = platformList
    }
}