package com.github.kr328.clash.service.model

import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

class Subscription(
    val name: String,
    val price: Double,
    val activationDays: Int
) : BaseEntity()