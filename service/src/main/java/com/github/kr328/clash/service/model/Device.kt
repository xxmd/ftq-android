package com.github.kr328.clash.service.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class Device {
    lateinit var model: String
    lateinit var manufacturer: String
}