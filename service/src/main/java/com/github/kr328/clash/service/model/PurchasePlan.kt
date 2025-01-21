package com.github.kr328.clash.service.model

/**
 * 购买计划
 */
data class PurchasePlan(
    // 买那个套餐
    val subscription: Subscription,
    //
    val sku: Sku
)