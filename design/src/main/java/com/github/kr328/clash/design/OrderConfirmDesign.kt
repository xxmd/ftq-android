package com.github.kr328.clash.design

import android.content.Context
import android.view.View
import com.github.kr328.clash.design.databinding.DesignOrderConfirmBinding
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root

class OrderConfirmDesign(context: Context) : Design<OrderConfirmDesign.Request>(context) {

    sealed class Request {
        object OnConfirm : Request()
        object ToActivationCodeInputPage : Request()
    }

    val binding = DesignOrderConfirmBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.self = this
        binding.activityBarLayout.applyFrom(context)
    }

    fun onConfirm() {
        requests.trySend(Request.OnConfirm)
    }

    fun toActivationCodeInputPage() {
        requests.trySend(Request.ToActivationCodeInputPage)
    }
}