package com.github.kr328.clash.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

object Global : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val application: Application
        get() = application_
    val curActivity: Activity
        get() = curActivity_

    private lateinit var application_: Application
    private lateinit var curActivity_: Activity

    fun init(application: Application) {
        this.application_ = application
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // 在 Activity 创建时，保存当前 Activity
                curActivity_ = activity
            }

            override fun onActivityStarted(activity: Activity) {
                curActivity_ = activity
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                // 在 Activity 销毁时，清空当前 Activity 引用
//                if (curActivity == activity) {
//                }
            }
        })
    }

    fun destroy() {
        cancel()
    }
}