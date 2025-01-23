package com.github.kr328.clash.design.api.base

import com.github.kr328.clash.design.dialog.DialogManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.net.ssl.HttpsURLConnection

class SimpleCallback<T>(
    private val onSuccess: (data: T?) -> Unit,
    private val onError: (code: Int, message: String) -> Unit = { _, _ -> }
) : Callback<ApiResponse<T>> {
    override fun onResponse(call: Call<ApiResponse<T>>, response: Response<ApiResponse<T>>) {
        if (response.isSuccessful) {
            val apiResponse = response.body()
            if (apiResponse != null) {
                if (apiResponse.code == HttpsURLConnection.HTTP_OK) { // 假设 200 是成功的状态码
                    onSuccess(apiResponse.data)
                } else {
                    onError(apiResponse.code, apiResponse.message)
                }
            } else {
                onError(-1, "Response body is null")
            }
        } else {
            onServerError(response)
        }
    }

    override fun onFailure(call: Call<ApiResponse<T>>, t: Throwable) {
        onNetworkError(t)
    }

    private fun onServerError(response: Response<ApiResponse<T>>) {
        DialogManager.showErrorDialog("服务器异常", response.toString())
    }

    private fun onNetworkError(t: Throwable) {
        DialogManager.showErrorDialog("网络请求异常", t.toString())
    }
}
