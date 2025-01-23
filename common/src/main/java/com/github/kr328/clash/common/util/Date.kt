package com.github.kr328.clash.common.util

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String): String {
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(this)
}

fun Date.format(): String {
    return this.format("yyyy-MM-dd HH:mm:ss")
}
