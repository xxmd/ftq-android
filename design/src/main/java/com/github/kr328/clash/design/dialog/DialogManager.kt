package com.github.kr328.clash.design.dialog

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Handler
import android.os.Looper
import com.github.kr328.clash.common.Global

object DialogManager {
    private var loadingDialog: ProgressDialog? = null
    private var errorDialog: AlertDialog? = null

    // 显示loading的Dialog
    fun showLoadingDialog(message: String = "加载中...") {
        runOnUiThread {
            if (loadingDialog == null) {
                loadingDialog = ProgressDialog(Global.curActivity).apply {
                    setMessage(message)
                    setCancelable(false)  // 不允许点击关闭
                    setIndeterminate(true) // 不确定的进度条
                }
            }
            loadingDialog?.show()
        }
    }

    // 关闭loading的Dialog
    fun hideLoadingDialog() {
        runOnUiThread {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    // 显示错误信息的Dialog
    fun showErrorDialog(title: String, errorMessage: String, onDismiss: (() -> Unit)? = null) {
        runOnUiThread {
            if (errorDialog == null) {
                errorDialog = AlertDialog.Builder(Global.curActivity)
                    .setTitle(title)
                    .setMessage(errorMessage)
                    .setCancelable(true)
                    .setPositiveButton("确定") { _, _ ->
                        // 错误弹窗被点击时的操作
                        onDismiss?.invoke() // 如果提供了dismiss回调，执行
                        errorDialog?.dismiss()
                        errorDialog = null
                    }
                    .create()
            }
            errorDialog?.show()
        }
    }

    private fun runOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post {
            action()
        }
    }
}
