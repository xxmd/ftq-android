package com.github.kr328.clash.service.model

import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

data class PurchasePlan(
    val subscription: Subscription,
    val paymentPlatform: PaymentPlatform,
    val sku: Sku
)