@file:UseSerializers(UUIDSerializer::class)

package com.github.kr328.clash.service.model

import android.os.Parcel
import android.os.Parcelable
import com.github.kr328.clash.core.util.Parcelizer
import com.github.kr328.clash.service.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.*

@Serializable
data class Subscription(
    val id: Int,
    val name: String,
    val price: Double,
    val activationDays: Int,
    val selected: Boolean
)