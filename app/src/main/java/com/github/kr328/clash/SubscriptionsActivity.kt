package com.github.kr328.clash

import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.design.SubscriptionsDesign
import java.util.concurrent.TimeUnit


class SubscriptionsActivity : BaseActivity<SubscriptionsDesign>() {

    override suspend fun main() {
        val design = SubscriptionsDesign(this)

        setContentDesign(design)

        val ticker = ticker(TimeUnit.SECONDS.toMillis(1))

    }

    private suspend fun SubscriptionsDesign.fetch() {

    }

}