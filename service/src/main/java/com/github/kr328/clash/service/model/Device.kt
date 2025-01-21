package com.github.kr328.clash.service.model

class Device(
    val androidId: String,
    val model: String,
    val manufacturer: String,
    val apiLevel: Int
) : BaseEntity()