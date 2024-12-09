package com.github.kr328.clash.service.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date
import kotlin.random.Random

@Serializable
open class BaseEntity {
    var id: Int = Random.nextInt(1, Int.MAX_VALUE)

    @Contextual
    var createTime: Date = Date()

    @Contextual
    var updateTime: Date = Date()
}