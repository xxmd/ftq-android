package com.github.kr328.clash.design.api.base

import com.github.kr328.clash.design.dialog.DialogManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    private const val BASE_URL = "http://192.168.100.214:8080"
    private val loadingInterceptor =
        LoadingInterceptor(ApiClient::showLoading, ApiClient::hideLoading)

    private fun showLoading() {
        DialogManager.showLoadingDialog("网络请求中...")
    }

    private fun hideLoading() {
        DialogManager.hideLoadingDialog()
    }

    val instance: Retrofit by lazy {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(loadingInterceptor)
                    .build()
            )
            .build()
    }

    inline fun <reified T> create(): T {
        return instance.create(T::class.java)
    }
}

