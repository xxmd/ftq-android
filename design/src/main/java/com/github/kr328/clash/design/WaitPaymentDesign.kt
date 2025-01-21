package com.github.kr328.clash.design

import android.content.Context
import android.view.View
import com.github.kr328.clash.design.databinding.DesignWaitPaymentBinding
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root

class WaitPaymentDesign(context: Context) : Design<WaitPaymentDesign.Request>(context) {
    sealed class Request {
    }

    private val binding = DesignWaitPaymentBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.self = this

        binding.activityBarLayout.applyFrom(context)


    }

    fun confirmPayment() {

    }

    fun cancelPayment() {

    }

}