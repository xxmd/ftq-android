import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ExpiryService : Service() {

    private var expiryDate: Calendar = Calendar.getInstance()

    // 获取当前有效期
    fun getRemainingValidity(): String {
        val now = Calendar.getInstance()
        val remainingTime = expiryDate.timeInMillis - now.timeInMillis
        return if (remainingTime > 0) {
            val remainingDays = remainingTime / (1000 * 60 * 60 * 24) // 转换为天数
            "剩余有效期: $remainingDays 天"
        } else {
            "有效期已过"
        }
    }

    // 新增有效期天数
    fun addValidityDays(days: Int) {
        expiryDate.add(Calendar.DAY_OF_YEAR, days)
        println("有效期已更新，新有效期为: ${expiryDate.time}")
    }

    // 每半个小时检查有效期
    private fun startExpiryCheck() {
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleWithFixedDelay({
            val now = Calendar.getInstance()
            if (now.after(expiryDate)) {
                println("警告：有效期已超过！")
            } else {
                println("当前有效期未过")
            }
        }, 0, 30, TimeUnit.MINUTES) // 每30分钟检查一次
    }

    override fun onCreate() {
        super.onCreate()
        startExpiryCheck() // 启动定时检查
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // 这个 Service 不提供绑定接口
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 在此处理传入的 Intent
        return START_STICKY
    }
}
