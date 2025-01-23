package com.github.kr328.clash.design

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.common.util.format
import com.github.kr328.clash.design.api.ActivationCodeApi
import com.github.kr328.clash.design.api.base.ApiClient
import com.github.kr328.clash.design.api.base.SimpleCallback
import com.github.kr328.clash.design.databinding.DesignActivationCodeInputBinding
import com.github.kr328.clash.design.dialog.DialogManager
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.design.util.ValidatorUUIDString
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.cancelTextInput
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.requestTextInput
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.ActivationCode
import com.github.kr328.clash.service.model.Device
import com.github.kr328.clash.service.store.ServiceStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import com.google.gson.Gson as Gson1

class ActivationCodeInputDesign(context: Context) :
    Design<ActivationCodeInputDesign.Request>(context) {
    val service = ServiceStore(context)
    val activationCodeApi = ApiClient.create<ActivationCodeApi>()

    sealed class Request {
        object OnActivationCodeApply : Request()
    }

    val binding = DesignActivationCodeInputBinding
        .inflate(context.layoutInflater, context.root, false)

    val errorMessage = "激活码格式错误"
    private var daoActivationCode: ActivationCode? = null
    override val root: View
        get() = binding.root

    init {
        binding.self = this
        binding.activityBarLayout.applyFrom(context)
        binding.textField.apply {
            binding.textLayout.isErrorEnabled = errorMessage != null

            doOnTextChanged { text, _, _, _ ->
                binding.textField.setText(text?.trim())
                binding.btnClearInput.isEnabled = !text.isNullOrEmpty()
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

    private fun showCodeNotExistDialog() {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.activation_code_validate_error)
            .setMessage(
                "该激活码不存在\n" +
                        "1. 请自行检查\n" +
                        "2. 确认无误联系商家或佛跳墙群主"
            )
            .setPositiveButton(R.string.ok) { _, _ -> }
            .show()
    }

    private fun showCodeBeUsedDialog(activationCode: ActivationCode) {
        MaterialAlertDialogBuilder(context)
            .setTitle("该激活码已被使用")
            .setMessage(
                String.format(
                    "激活记录如下\n" +
                            "激活日期：%s\n" +
                            "激活设备：%s",
                    activationCode.activateTime.format(),
                    activationCode.device!!.model
                )
            )
            .setPositiveButton(R.string.ok) { _, _ -> }
            .show()
    }

    private fun showCodeConfirmDialog(activationCode: ActivationCode) {
        MaterialAlertDialogBuilder(context)
            .setTitle("激活码信息")
            .setMessage(
                String.format(
                    "所属套餐：%s\n" +
                            "激活时长：%d天\n",
                    activationCode.subscription.name,
                    activationCode.subscription.activationDays
                )
            )
            .setPositiveButton("确认激活") { _, _ -> onConfirmActivate(activationCode) }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }

    private fun onConfirmActivate(activationCode: ActivationCode) {
        // 发送网络请求将激活码状态转成已激活，并存储激活记录
        binding.textField.cancelTextInput()
        activate(activationCode, SimpleCallback(onSuccess = { data ->
            onActivateSuccess(activationCode)
        }, ::onActivateFailed))
    }

    private fun onActivateSuccess(activationCode: ActivationCode) {
        val baseDate = getMaxDate(service.expirationDate, Date())
        val calendar = Calendar.getInstance()
        calendar.time = baseDate
        calendar.add(Calendar.DAY_OF_YEAR, activationCode.subscription.activationDays)
        MaterialAlertDialogBuilder(context)
            .setTitle("激活成功")
            .setMessage(
                String.format(
                    "原始的有效期：%s\n" +
                            "新增激活时长：%d天\n" +
                            "新增后有效期：%s",
                    service.expirationDate.format(),
                    activationCode.subscription.activationDays,
                    calendar.time.format()
                )
            )
            .setPositiveButton(R.string.ok) { _, _ ->
                setExpirationDate(calendar.time)
            }
            .show()
    }

    private fun onActivateFailed(code: Int, message: String) {
        DialogManager.showErrorDialog("激活失败", message)
    }

    private fun activate(activationCode: ActivationCode, simpleCallback: SimpleCallback<String>) {
        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val device =
            Device(androidId, Build.MODEL, Build.MANUFACTURER, Build.VERSION.SDK_INT)
        activationCode.device = device
        var call = activationCodeApi.activate(activationCode)
        call.enqueue(simpleCallback)
    }

    private fun getMaxDate(date1: Date, date2: Date): Date {
        return if (date1 >= date2) date1 else date2
    }

    private fun setExpirationDate(date: Date) {
        // 发送网络请求将激活码状态转成已激活，并存储激活记录
        service.expirationDate = date
        GlobalScope.launch(Dispatchers.Main) {
            showToast("有效期延长至 " + date.format(), ToastDuration.Indefinite) {
                setAction(R.string.ok) {
                    requests.trySend(Request.OnActivationCodeApply)
                }
            }
        }
    }

    fun onConfirm() {
        val content = binding.textField.text!!.trim().toString()
        val call = activationCodeApi.findByContent(content)
        call.enqueue(SimpleCallback(
            onSuccess = { data ->
                daoActivationCode = data
                if (daoActivationCode!!.device != null) {
                    // 激活码已经被使用
                    showCodeBeUsedDialog(daoActivationCode!!)
                } else {
                    // 激活码存在且未被使用
                    showCodeConfirmDialog(daoActivationCode!!)
                }
            },
            onError = { code, message ->
                showCodeNotExistDialog()
            }
        ))
    }

    fun clearInput() {
        binding.textField.setText("")
    }
}