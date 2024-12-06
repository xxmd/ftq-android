package com.github.kr328.clash.service.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
open class BaseEntity {
    var id: Int = 0

    @Contextual
    lateinit var createTime: Date

    @Contextual
    lateinit var updateTime: Date
}