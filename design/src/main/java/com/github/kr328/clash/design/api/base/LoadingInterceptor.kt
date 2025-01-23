package com.github.kr328.clash.design.api.base

import okhttp3.Interceptor
import okhttp3.Response

class LoadingInterceptor(
    private val onStartLoading: () -> Unit,
    private val onStopLoading: () -> Unit
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 请求开始时显示 loading
        onStartLoading()

        try {
            // 执行请求
            val response = chain.proceed(chain.request())
            // 请求完成时隐藏 loading
            return response
        } catch (e: Exception) {
            // 请求出错时也隐藏 loading
            onStopLoading()
            throw e  // 继续抛出异常
        } finally {
            // 请求完成时隐藏 loading，无论请求成功还是失败
            onStopLoading()
        }
    }
}
