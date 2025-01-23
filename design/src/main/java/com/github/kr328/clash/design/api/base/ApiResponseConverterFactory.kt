package com.github.kr328.clash.design.api.base

import com.github.kr328.clash.design.dialog.DialogManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.net.ssl.HttpsURLConnection

/**
 * 将 responseBody 转成 apiResponse 对象，取出其中的 data 数据
 * responseBody -> ApiResponse -> ApiResponse.data
 */
class ApiResponseConverterFactory : Converter.Factory() {
    private val gson: Gson = Gson()

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return Converter { responseBody ->
            // 将 ResponseBody 转换为 ApiResponse
            val json = responseBody.string()
            val apiResponseType = TypeToken.getParameterized(ApiResponse::class.java, type).type
            val apiResponse: ApiResponse<Any> = gson.fromJson(json, apiResponseType)
            when (apiResponse.code) {
                HttpsURLConnection.HTTP_BAD_REQUEST -> {
                    DialogManager.showErrorDialog("", apiResponse.message)
                }
            }
            return@Converter apiResponse.data
        }
    }
}