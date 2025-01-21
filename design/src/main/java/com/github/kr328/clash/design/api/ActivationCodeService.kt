package com.github.kr328.clash.design.api

import com.github.kr328.clash.service.model.ActivationCode
import com.github.kr328.clash.service.model.Sku
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 激活码相关接口
 */
interface ActivationCodeService {
    @GET("/code/findByContent")
    suspend fun findByContent(@Query("codeContent") codeContent: String): ApiResponse<ActivationCode>
}