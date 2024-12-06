package com.github.kr328.clash.service.model

import kotlinx.serialization.Serializable

@Serializable
class ActivationRecord : BaseEntity() {
    lateinit var device: Device
    lateinit var subscriptionId: String
    lateinit var activationCodeId: String
}