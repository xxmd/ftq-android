@file:UseSerializers(UUIDSerializer::class)

package com.github.kr328.clash.service.model

import com.github.kr328.clash.service.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

class Platform(
    val name: String,
    val iconUrl: String,
    val packageName: String,
) : BaseEntity()