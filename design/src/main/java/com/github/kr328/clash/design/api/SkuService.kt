package com.github.kr328.clash.design.api

import com.github.kr328.clash.service.model.Sku
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * sku 相关接口
 */
interface SkuService {
    @GET("/Sku/findAllBySubscriptionId")
    suspend fun findAllBySubscriptionId(@Query("subscriptionId") subscriptionId: Long): ApiResponse<List<Sku>>
}