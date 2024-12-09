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
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.design.util.ValidatorUUIDString
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.applyLinearAdapter
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.patchDataSet
import com.github.kr328.clash.design.util.requestTextInput
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.ActivationCode
import com.github.kr328.clash.service.model.ActivationRecord
import com.github.kr328.clash.service.model.Device
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.service.model.Sku
import com.github.kr328.clash.service.model.Subscription
import com.github.kr328.clash.service.store.ServiceStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ActivationCodeInputDesign(context: Context) :
    Design<ActivationCodeInputDesign.Request>(context) {
    val service = ServiceStore(context)

    sealed class Request {
        object OnConfirm : Request()
    }

    val binding = DesignActivationCodeInputBinding
        .inflate(context.layoutInflater, context.root, false)

    val errorMessage = "激活码格式错误"

    override val root: View
        get() = binding.root

    init {
        binding.self = this
        binding.activityBarLayout.applyFrom(context)
        binding.textField.apply {
            binding.textLayout.isErrorEnabled = errorMessage != null

            doOnTextChanged { text, _, _, _ ->
                if (ValidatorUUIDString(text?.toString() ?: "")) {
                    binding.textLayout.error = null
                    binding.btnConfirm.isEnabled = true
                } else {
                    binding.textLayout.error = errorMessage
                    binding.btnConfirm.isEnabled = false
                }
            }

            requestTextInput()
        }
    }

    fun showCodeNotExistDialog() {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.activation_code_validate_error)
            .setMessage(
                "该激活码不存在\n" +
                        "1. 请自行检查" +
                        "2. 确认无误联系商家或佛跳墙群主"
            )
            .setPositiveButton(R.string.ok) { _, _ -> }
            .show()
    }

    fun showCodeBeUsedDialog(activationRecord: ActivationRecord) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.activation_code_validate_error)
            .setMessage(
                String.format(
                    "该激活码已被使用，激活记录如下\n" +
                            "激活日期：%s\n" +
                            "激活设备：%s",
                    formatDate(activationRecord.createTime),
                    activationRecord.device.model
                )
            )
            .setPositiveButton(R.string.ok) { _, _ -> }
            .show()
    }

    fun showCodeConfirmDialog(activationCode: ActivationCode) {
        MaterialAlertDialogBuilder(context)
            .setTitle("激活码信息")
            .setMessage(
                String.format(
                    "所属套餐：%s\n" +
                            "激活时长：%d天\n",
                    activationCode.subscription.name,
                    activationCode.activationDays
                )
            )
            .setPositiveButton("确认激活") { _, _ -> showExpirationDateWillAddDialog(activationCode) }
            .show()
    }

    fun showExpirationDateWillAddDialog(activationCode: ActivationCode) {
        // 发送网络请求将激活码状态转成已激活，并存储激活记录
        val calendar = Calendar.getInstance()
        calendar.time = service.expirationDate
        calendar.add(Calendar.DAY_OF_YEAR, activationCode.activationDays)
        MaterialAlertDialogBuilder(context)
            .setTitle("激活成功")
            .setMessage(
                String.format(
                    "原始的有效期：%s\n" +
                            "新增激活时长：%d天\n" +
                            "新增后有效期：%s",
                    formatDate(service.expirationDate),
                    activationCode.activationDays,
                    formatDate(calendar.time)
                )
            )
            .setPositiveButton(R.string.ok) { _, _ ->
                addExpirationDate(activationCode.activationDays)
            }
            .show()
    }

    fun addExpirationDate(addDays: Int) {
        // 发送网络请求将激活码状态转成已激活，并存储激活记录
        val calendar = Calendar.getInstance()
        calendar.time = service.expirationDate
        calendar.add(Calendar.DAY_OF_YEAR, addDays)
        service.expirationDate = calendar.time
        GlobalScope.launch(Dispatchers.Main) {
            showToast("有效期延长至 " + formatDate(calendar.time), ToastDuration.Indefinite) {
                setAction(R.string.ok) {
                }
            }
        }
    }

    fun onConfirm() {
        val content = binding.textField.text!!.trim().toString()
        val activationCode = queryActivationCode(content)
        /**
         * 发起请求查询该激活码状态
         * 1. 该激活码不存在
         * 2. 激活码存在
         *  2.1 存在激活记录，即已经被其他设备使用
         *  2.2 没有激活记录，是一个全新的激活码
         */
        if (activationCode == null) {
            showCodeNotExistDialog()
        } else if (activationCode.activationRecord != null) {
            showCodeBeUsedDialog(activationCode.activationRecord!!)
        } else {
            showCodeConfirmDialog(activationCode)
        }

        requests.trySend(Request.OnConfirm)
    }

    fun formatDate(date: Date): String {
        Log.i("formatDate: " + date)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return formatter.format(date)
    }

    fun queryActivationCode(codeContent: String): ActivationCode? {
        val activationCode = ActivationCode()
//        activationCode.activationDays = 30
//        activationCode.subscription = Subscription()
//        activationCode.subscription.name = "包月套餐"
        return activationCode
    }
}