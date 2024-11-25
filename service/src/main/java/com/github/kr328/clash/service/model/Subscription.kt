
package com.github.kr328.clash.service.model

import kotlinx.serialization.Serializable

@Serializable
data class Subscription(
    val id: Int,
    val name: String,
    val price: Double,
    val activationDays: Int,
)