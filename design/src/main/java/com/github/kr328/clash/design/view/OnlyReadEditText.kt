package com.github.kr328.clash.design.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.github.kr328.clash.design.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class OnlyReadEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    init {
        // 设置文本框为只读
        val textInputEditText = TextInputEditText(context).apply {
            isFocusable = false
            isClickable = false
            isCursorVisible = false
            setTextIsSelectable(false) // 禁止选中
        }

        boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE

        // 将 TextInputEditText 添加到 TextInputLayout 中
        addView(textInputEditText)

        // 获取自定义的属性
        context.theme.obtainStyledAttributes(attrs, R.styleable.OnlyReadEditText, 0, 0).apply {
            try {
                val customText = getString(R.styleable.OnlyReadEditText_text)
                val customHint = getString(R.styleable.OnlyReadEditText_hint)

                textInputEditText.setText(customText)
                textInputEditText.setHint(customHint)
            } finally {
                recycle()
            }
        }
    }

    // 设置文本和提示
    fun setTextAndHint(text: String, hint: String) {
        (getChildAt(0) as TextInputEditText).apply {
            setText(text)
            setHint(hint)
        }
    }
}
