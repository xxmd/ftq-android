package com.github.kr328.clash.design.api

import javax.net.ssl.HttpsURLConnection

class ApiResponse<T> {
    val code: Int
    val message: String
    val data: T?

    constructor(code: Int, message: String, data: T?) {
        this.code = code
        this.message = message
        this.data = data
    }

    fun isSuccess(): Boolean {
        return code == HttpsURLConnection.HTTP_OK
    }
}