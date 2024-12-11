package com.github.kr328.clash.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.kr328.clash.common.compat.getColorCompat
import com.github.kr328.clash.common.compat.pendingIntentFlags
import com.github.kr328.clash.common.constants.Components
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.common.util.format
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.service.clash.module.StaticNotificationModule
import com.github.kr328.clash.service.store.ServiceStore
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 有效期实时检测服务
 */
class ExpirationCheckService : Service() {
    private lateinit var serviceStore:ServiceStore

    // 每半个小时检查有效期
    private fun loopCheckExpirationDate() {
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleWithFixedDelay({
            Log.i("loopCheckExpirationDate")
            val now = Date()
            if (serviceStore.expirationDate > now) {
                Log.i("ExpirationCheckService 当前有效期未过")
            } else {
                onOverExpiration()
            }
        }, 0, 5, TimeUnit.SECONDS) // 每30分钟检查一次
    }

    private fun onOverExpiration() {
        Log.i("onOverExpiration")
        createNotificationChannel()
        showNotification()
    }

    companion object {
        private val CHANNEL_ID = ExpirationCheckService::class.simpleName!!

        var running: Boolean = false
    }

    private fun createNotificationChannel() {
        NotificationManagerCompat.from(this)
            .createNotificationChannel(
                NotificationChannelCompat.Builder(
                    CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_DEFAULT
                ).setName(CHANNEL_ID).build()
            )
    }

    private fun showNotification() {
        val notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_service)
            .setColor(getColorCompat(R.color.color_clash))
            .setContentTitle("你的有效期不足，请及时充值")
            .setContentText(getString(R.string.running))
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    R.id.nf_clash_status,
                    Intent().setComponent(Components.MAIN_ACTIVITY)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                    pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
                )
            )
            .build()
        startForeground(R.id.nf_clash_status, notification)
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("ExpirationCheckService onCreate")
        serviceStore = ServiceStore(this)
        loopCheckExpirationDate() // 启动定时检查
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // 这个 Service 不提供绑定接口
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}