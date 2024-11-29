package com.github.kr328.clash.service.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class ActivationCode(
    val id: Int,
    val content: String,
    val activationDays: Int,
    val  subscriptionId: Int,
    @Contextual val exportTime: Date,
    @Contextual val activateTime: Date
)