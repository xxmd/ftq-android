package com.github.kr328.clash.design.util

import com.github.kr328.clash.common.util.PatternFileName

typealias Validator = (String) -> Boolean

val ValidatorAcceptAll: Validator = {
    true
}

val ValidatorFileName: Validator = {
    PatternFileName.matches(it) && it.isNotBlank()
}

val ValidatorNotBlank: Validator = {
    it.isNotBlank()
}

val ValidatorHttpUrl: Validator = {
    it.startsWith("https://", ignoreCase = true) || it.startsWith("http://", ignoreCase = true)
}

val ValidatorAutoUpdateInterval: Validator = {
    it.isEmpty() || (it.toLongOrNull() ?: 0) >= 15
}

val ValidatorUUIDString: Validator = { input ->
    try {
        // 尝试将字符串解析为 UUID
        java.util.UUID.fromString(input)
        true
    } catch (e: IllegalArgumentException) {
        // 如果解析失败，说明不是有效的 UUID
        false
    }
}