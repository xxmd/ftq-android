package com.github.kr328.clash.design.api

import com.github.kr328.clash.design.ActivationCodeInputDesign
import com.github.kr328.clash.design.api.base.ApiResponse
import com.github.kr328.clash.service.model.ActivationCode
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 激活码相关接口
 */
interface ActivationCodeApi {
    @GET("/activationCode/findByContent")
    fun findByContent(@Query("codeContent") codeContent: String): Call<ApiResponse<ActivationCode>>

    @POST("/activationCode/activate")
    fun activate(@Body activationCode: ActivationCode): Call<ApiResponse<String>>
}