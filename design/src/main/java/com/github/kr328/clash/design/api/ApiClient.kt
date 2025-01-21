package com.github.kr328.clash.design.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.214:8080"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val subscriptionService: SubscriptionService by lazy {
        RetrofitClient.retrofit.create(SubscriptionService::class.java)
    }
    val skuService: SkuService by lazy {
        RetrofitClient.retrofit.create(SkuService::class.java)
    }
}