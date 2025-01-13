package com.github.kr328.clash.design.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BaseApiService {
    // 创建 Retrofit 实例并根据泛型返回不同的 Service
     inline fun <reified T> create(): T {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8080")  // 根据实际情况修改 baseUrl
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(T::class.java)  // 使用泛型来创建 Service 实例
    }
}