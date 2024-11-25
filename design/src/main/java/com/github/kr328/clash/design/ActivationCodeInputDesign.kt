package com.github.kr328.clash.design

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.ProxyDesign.Request
import com.github.kr328.clash.design.adapter.SingleSelectAdapter
import com.github.kr328.clash.design.adapter.SubscriptionAdapter
import com.github.kr328.clash.design.databinding.DesignActivationCodeInputBinding
import com.github.kr328.clash.design.databinding.DesignOrderConfirmBinding
import com.github.kr328.clash.design.databinding.DesignSubscriptionsBinding
import com.github.kr328.clash.design.databinding.DialogPlatformMenuBinding
import com.github.kr328.clash.design.databinding.DialogProfilesMenuBinding
import com.github.kr328.clash.design.dialog.AppBottomSheetDialog
import com.github.kr328.clash.design.util.ValidatorUUIDString
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.applyLinearAdapter
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.patchDataSet
import com.github.kr328.clash.design.util.requestTextInput
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ActivationCodeInputDesign(context: Context) : Design<ActivationCodeInputDesign.Request>(context) {

    sealed class Request {
        object OnConfirm : Request()
    }

    val binding = DesignActivationCodeInputBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.self = this
        binding.activityBarLayout.applyFrom(context)
        binding.textField.apply {
            binding.textLayout.isErrorEnabled = error != null

            doOnTextChanged { text, _, _, _ ->
                if (!ValidatorUUIDString(text?.toString() ?: "")) {
                    if (error != null)
                        binding.textLayout.error = error

//                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                } else {
                    if (error != null)
                        binding.textLayout.error = null

//                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                }
            }

            requestTextInput()
        }
    }

    fun onConfirm() {
        requests.trySend(Request.OnConfirm)
    }


}