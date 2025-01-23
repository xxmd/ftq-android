package com.github.kr328.clash.design.api

import com.github.kr328.clash.design.api.base.ApiResponse
import com.github.kr328.clash.service.model.Subscription
import retrofit2.Call
import retrofit2.http.GET

// 定义 API 接口
interface SubscriptionApi {
    @GET("/subscription/findAll")
    fun findAll(): Call<ApiResponse<List<Subscription>>>
}