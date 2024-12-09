package com.github.kr328.clash.design

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import com.github.kr328.clash.common.util.format
import com.github.kr328.clash.core.model.FetchStatus
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.core.util.trafficTotal
import com.github.kr328.clash.design.databinding.DesignAboutBinding
import com.github.kr328.clash.design.databinding.DesignMainBinding
import com.github.kr328.clash.design.dialog.ModelProgressBarConfigure
import com.github.kr328.clash.design.dialog.withModelProgressBar
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.resolveThemedColor
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.store.ServiceStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainDesign(context: Context) : Design<MainDesign.Request>(context) {
    enum class Request {
        ToggleStatus,
        OpenProxy,
        OpenProfiles,
        OpenProviders,
        OpenLogs,
        OpenSettings,
        OpenHelp,
        OpenAbout,
        CopyQQGroupCount,
        PURCHASE,
    }

    val service = ServiceStore(context)

    private val binding = DesignMainBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    suspend fun setProfileName(name: String?) {
        withContext(Dispatchers.Main) {
            binding.profileName = name
        }
    }

    suspend fun setClashRunning(running: Boolean) {
        withContext(Dispatchers.Main) {
            binding.clashRunning = running
        }
    }

    suspend fun setForwarded(value: Long) {
        withContext(Dispatchers.Main) {
            binding.forwarded = value.trafficTotal()
        }
    }

    suspend fun setMode(mode: TunnelState.Mode) {
        withContext(Dispatchers.Main) {
            binding.mode = when (mode) {
                TunnelState.Mode.Direct -> context.getString(R.string.direct_mode)
                TunnelState.Mode.Global -> context.getString(R.string.global_mode)
                TunnelState.Mode.Rule -> context.getString(R.string.rule_mode)
                else -> context.getString(R.string.rule_mode)
            }
        }
    }

    suspend fun setHasProviders(has: Boolean) {
        withContext(Dispatchers.Main) {
            binding.hasProviders = has
        }
    }

    suspend fun showAbout(versionName: String) {
        withContext(Dispatchers.Main) {
            val binding = DesignAboutBinding.inflate(context.layoutInflater).apply {
                this.versionName = versionName
            }

            AlertDialog.Builder(context)
                .setView(binding.root)
                .show()
        }
    }

    init {
        binding.self = this

        binding.colorClashStarted = context.resolveThemedColor(R.attr.colorPrimary)
        binding.colorClashStopped = context.resolveThemedColor(R.attr.colorClashStopped)
        updateExpiration()
    }

    fun updateExpiration() {
        val (remainTime, hasRemaining) = computeRemain()
        binding.tvExpiration.text = String.format("剩余时间: %s", remainTime)
        binding.tvExpiration.subtext =
            String.format("有效期至: %s", service.expirationDate.format())
        if (!hasRemaining) {
            GlobalScope.launch(Dispatchers.Main) {
                showToast("您的使用有效期不足，请尽快充值", ToastDuration.Indefinite) {
                    setAction(R.string.ok) {
                        requests.trySend(Request.PURCHASE)
                    }
                }
            }
        }
    }

    private fun computeRemain(): Pair<String, Boolean> {
        val remainMillis = service.expirationDate.time - Date().time
        if (remainMillis <= 0) {
            return Pair("0 小时", false)  // 如果过期，返回 0 天 0 小时
        }

        // 计算剩余天数
        val remainDays = (remainMillis / (1000 * 60 * 60 * 24)).toInt()

        // 计算剩余小时数
        val remainHours = ((remainMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)).toInt()

        return Pair("$remainDays 天 $remainHours 小时", true)
    }

    fun request(request: Request) {
        requests.trySend(request)
    }

    suspend fun withProcessing(executeTask: suspend (suspend (FetchStatus) -> Unit) -> Unit) {
        try {
            context.withModelProgressBar {
                configure {
                    isIndeterminate = true
                    text = context.getString(R.string.initializing)
                }

                executeTask {
                    configure {
                        applyFrom(it)
                    }
                }
            }
        } finally {
        }
    }

    private fun ModelProgressBarConfigure.applyFrom(status: FetchStatus) {
        when (status.action) {
            FetchStatus.Action.FetchConfiguration -> {
                text = context.getString(R.string.format_fetching_configuration, status.args[0])
                isIndeterminate = true
            }

            FetchStatus.Action.FetchProviders -> {
                text = context.getString(R.string.format_fetching_provider, status.args[0])
                isIndeterminate = false
                max = status.max
                progress = status.progress
            }

            FetchStatus.Action.Verifying -> {
                text = context.getString(R.string.verifying)
                isIndeterminate = false
                max = status.max
                progress = status.progress
            }
        }
    }

    suspend fun copyQQGroupCount() {
        val data =
            ClipData.newPlainText("QQGroupCount", context.getString(R.string.qq_group_account))
        context.getSystemService<ClipboardManager>()?.setPrimaryClip(data)
        showToast("复制群号成功", ToastDuration.Long) {
            setAction(R.string.ok) {
            }
        }
    }
}