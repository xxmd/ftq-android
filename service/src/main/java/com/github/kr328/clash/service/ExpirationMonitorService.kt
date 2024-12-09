package com.github.kr328.clash.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.github.kr328.clash.common.compat.getColorCompat
import com.github.kr328.clash.common.compat.pendingIntentFlags
import com.github.kr328.clash.common.constants.Components
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.service.clash.module.StaticNotificationModule
import com.github.kr328.clash.service.store.ServiceStore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask

class ExpirationMonitorService : Service() {
    private val self: ExpirationMonitorService
        get() = this

    val service = ServiceStore(this)
    private val notificationManager = NotificationManagerCompat.from(this)

    private val builder = NotificationCompat.Builder(this, StaticNotificationModule.CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo_service)
        .setOngoing(true)
        .setColor(getColorCompat(R.color.color_clash))
        .setOnlyAlertOnce(true)
        .setShowWhen(false)
        .setContentTitle("使用天数已超限")
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                R.id.nf_clash_status,
                Intent().setComponent(Components.MAIN_ACTIVITY)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
            )
        )

    override fun onCreate() {
        super.onCreate()
        getSystemService<AlarmManager>()

    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun checkOutOfDate() {
        val timer = Timer()
        val task = timerTask {
            val now = Date()
            if (service.expirationDate <= now) {
                showOutOfDateNotify()
                stopSelf()
            }
        }
        val period = 30 * 1000L
        timer.schedule(task, 0, period)
    }

    private fun showOutOfDateNotify() {
        val notification = builder
            .setContentText("套餐使用天数至: " + formatDate(service.expirationDate)
            )
            .build()
        notificationManager.notify(R.id.nf_clash_status, notification)
    }

    fun formatDate(date: Date): String {
        Log.i("formatDate: " + date)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return formatter.format(date)
    }
}
