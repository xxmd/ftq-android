@file:UseSerializers(UUIDSerializer::class)

package com.github.kr328.clash.service.model

import com.github.kr328.clash.service.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

data class Sku(
    val id: Int,
    val platformId: Int,
    val subscriptionId: Int,
    val link: String,
    val platform: Platform
)