package com.github.kr328.clash.design.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.github.kr328.clash.design.R

class ActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        // 设置上下 padding
        setPadding(0, 20.dpToPx(), 0, 20.dpToPx())
        background = createStateBackground()
        setTextColor(Color.WHITE)
        setTextSize(16f)
    }

    // 扩展函数：dp 转 px
    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    // 创建支持状态的背景
    private fun createStateBackground(): StateListDrawable {
        val enabledDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 8.dpToPx().toFloat() // 圆角半径
            setColor(ContextCompat.getColor(context, R.color.color_clash_light)) // 启用时背景颜色
        }

        val disabledDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 8.dpToPx().toFloat()
            setColor(ContextCompat.getColor(context, R.color.color_light_control_disabled)) // 禁用时背景颜色
        }

        return StateListDrawable().apply {
            addState(intArrayOf(-android.R.attr.state_enabled), disabledDrawable) // 禁用状态
            addState(intArrayOf(android.R.attr.state_enabled), enabledDrawable)  // 启用状态
        }
    }
}
