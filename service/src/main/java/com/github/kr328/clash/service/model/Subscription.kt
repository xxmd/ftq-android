package com.github.kr328.clash.service.model

import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

@Serializable
class Subscription : BaseEntity() {
    lateinit var name: String
    var price by Delegates.notNull<Double>()
    var activationDays by Delegates.notNull<Int>()
}