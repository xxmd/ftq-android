package com.github.kr328.clash.design

import android.app.ProgressDialog
import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.kr328.clash.design.ui.Surface
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.design.util.setOnInsertsChangedListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext

abstract class Design<R>(val context: Context) :
    CoroutineScope by CoroutineScope(Dispatchers.Unconfined) {
    abstract val root: View

    val surface = Surface()
    val requests: Channel<R> = Channel(Channel.UNLIMITED)
    var loadingDialog: ProgressDialog = ProgressDialog(context)

    suspend fun showToast(
        resId: Int,
        duration: ToastDuration,
        configure: Snackbar.() -> Unit = {}
    ) {
        return showToast(context.getString(resId), duration, configure)
    }

    suspend fun showToast(
        message: CharSequence,
        duration: ToastDuration,
        configure: Snackbar.() -> Unit = {}
    ) {
        withContext(Dispatchers.Main) {
            Snackbar.make(
                root,
                message,
                when (duration) {
                    ToastDuration.Short -> Snackbar.LENGTH_SHORT
                    ToastDuration.Long -> Snackbar.LENGTH_LONG
                    ToastDuration.Indefinite -> Snackbar.LENGTH_INDEFINITE
                }
            ).apply(configure).show()
        }
    }

    init {
        when (context) {
            is AppCompatActivity -> {
                context.window.decorView.setOnInsertsChangedListener {
                    if (surface.insets != it) {
                        surface.insets = it
                    }
                }
            }
        }
    }

    fun showLoadingDialog(message: String) {
        if (loadingDialog == null) {
            loadingDialog = ProgressDialog(context)
        }
        loadingDialog.setMessage(message)
        loadingDialog.show()
    }

    fun showLoadingDialog() {
        showLoadingDialog("加载中...")
    }

    fun closeLoadingDialog() {
        if (loadingDialog == null) {
            return
        }
        loadingDialog.dismiss()
    }

    fun showErrorDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle("出现错误") // 设置标题
            setMessage(errorMessage) // 设置错误消息
            setPositiveButton("确认") { dialog, _ ->
                dialog.dismiss()  // 按钮点击后关闭对话框
            }
        }

        // 创建并显示对话框
        val dialog = builder.create()
        dialog.show()
    }
}