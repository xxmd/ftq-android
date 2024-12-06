package com.github.kr328.clash.service.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class ActivationCode : BaseEntity() {
    var content: String = ""
    var activationDays: Int = 0
    var subscriptionId: Int = 0
    lateinit var subscription: Subscription

    @Contextual
    lateinit var exportTime: Date

    @Contextual
    lateinit var activateTime: Date
    var activationRecord: ActivationRecord? = null
}