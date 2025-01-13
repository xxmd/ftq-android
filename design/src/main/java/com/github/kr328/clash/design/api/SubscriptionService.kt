package com.github.kr328.clash.design.api

import com.github.kr328.clash.service.model.Subscription
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import javax.net.ssl.HttpsURLConnection

// 定义 API 接口
interface SubscriptionService {
    @GET("/subscription/findAll")
    suspend fun findAll(): ApiResponse<List<Subscription>>
}


// 创建 Retrofit 实例
fun createRetrofit(): SubscriptionService {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.172:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(SubscriptionService::class.java)
}