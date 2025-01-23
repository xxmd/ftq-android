package com.github.kr328.clash.design.api.base

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)