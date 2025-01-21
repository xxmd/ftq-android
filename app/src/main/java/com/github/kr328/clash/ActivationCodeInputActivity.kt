package com.github.kr328.clash

import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.design.ActivationCodeInputDesign
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

/**
 * 激活码输入界面
 */
class ActivationCodeInputActivity : BaseActivity<ActivationCodeInputDesign>() {

    override suspend fun main() {
        val design = ActivationCodeInputDesign(this)
        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                events.onReceive {

                }
                design.requests.onReceive {
                    when(it) {
                        ActivationCodeInputDesign.Request.OnActivationCodeApply -> {
                            toMainActivity()
                        }
                    }
                }
            }
        }
    }

    private fun toMainActivity() {
        startActivity(MainActivity::class.intent)
    }
}