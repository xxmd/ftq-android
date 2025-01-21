package com.github.kr328.clash.service.model

import java.util.*

class ActivationCode : BaseEntity() {
    // 激活码状态
    var status: ActivationCodeStatus = ActivationCodeStatus.CREATED

    // 激活码内容
    var content: String = ""

    // 所属订阅套餐
    var subscriptionId: Int = 0

    // 订阅套餐
    lateinit var subscription: Subscription

    // 导出时间
    lateinit var exportTime: Date

    // 激活时间
    lateinit var activateTime: Date

    // 激活设备
    var device: Device? = null
}