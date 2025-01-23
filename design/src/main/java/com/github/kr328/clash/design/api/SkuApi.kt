package com.github.kr328.clash.design.api

import com.github.kr328.clash.design.api.base.ApiResponse
import com.github.kr328.clash.service.model.Sku
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * sku 相关接口
 */
interface SkuApi {
    @GET("/sku/findAllBySubscriptionId")
    fun findAllBySubscriptionId(@Query("subscriptionId") subscriptionId: Long): Call<ApiResponse<List<Sku>>>
}