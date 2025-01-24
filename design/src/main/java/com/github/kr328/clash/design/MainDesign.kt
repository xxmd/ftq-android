package com.github.kr328.clash.design

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.constants.Permissions
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
import kotlinx.coroutines.delay
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
        JoinQQGroup,
        Purchase,
        Activate,
    }

    val service = ServiceStore(context)
    private var toastHasPopped = false
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
        loopCheckExpiration()

    }

    fun loopCheckExpiration() {
        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                val (remainTime, hasRemaining) = computeRemain()

                // 更新UI
                binding.tvExpiration.text = String.format("剩余: %s", remainTime)
                binding.tvExpiration.subtext =
                    String.format("有效期至: %s", service.expirationDate.format())

                binding.cardAppForbidden.visibility = if (!hasRemaining) View.VISIBLE else View.GONE
                binding.cardClashSwitch.visibility = if (hasRemaining) View.VISIBLE else View.GONE

                if (!hasRemaining && !toastHasPopped) {
                    showToast("您的使用有效期不足，请尽快充值", ToastDuration.Indefinite) {
                        setAction("前往充值") {
                            toastHasPopped = false
                            requests.trySend(Request.Purchase)
                        }
                    }
                    toastHasPopped = true
                }

                // 每五秒进行一次检查
                delay(5000L) // 5000毫秒 = 5秒
            }
        }
    }

    private fun computeRemain(): Pair<String, Boolean> {
        val remainMillis = service.expirationDate.time - Date().time
        if (remainMillis <= 0) {
            return Pair("0 小时 0 分钟", false)
        }
        // 使用毫秒计算剩余时间
        val totalMinutes = (remainMillis / (1000 * 60)).toInt()
        val remainDays = totalMinutes / (24 * 60) // 每天的分钟数
        val remainHours = (totalMinutes % (24 * 60)) / 60 // 每小时的分钟数
        val remainMinutes = totalMinutes % 60 // 剩余的分钟数
        return Pair("$remainDays 天 $remainHours 小时 $remainMinutes 分钟", true)
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

    fun joinQQGroup() {
        try {
            val key = "SSgZfrL-nWYuhZsboo261vM8Pn1eyXFV"
            val url =
                "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val qqGroupAccount =
                ClipData.newPlainText("QQGroupCount", context.getString(R.string.qq_group_account))
            context.getSystemService<ClipboardManager>()?.setPrimaryClip(qqGroupAccount)
            GlobalScope.launch(Dispatchers.Main) {
                showToast("无法自动加入，已将群号复制至剪切板", ToastDuration.Long) {
                    setAction(R.string.ok) {
                    }
                }
            }
        }
    }
}