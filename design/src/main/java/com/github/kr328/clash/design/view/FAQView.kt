package com.github.kr328.clash.design.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.bumptech.glide.Glide
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.databinding.ComponentActionLabelBinding
import com.github.kr328.clash.design.databinding.ComponentCellBinding
import com.github.kr328.clash.design.databinding.ComponentFaqBinding
import com.github.kr328.clash.design.util.layoutInflater

class FAQView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr, defStyleRes) {
    private val binding = ComponentFaqBinding
        .inflate(context.layoutInflater, this, true)

    var question: CharSequence? = null
        get() {
            return field
        }
        set(value) {
            field = value
            binding.tvQuestion.setText(value)
        }

    var answer: CharSequence? = null
        get() {
            return field
        }
        set(value) {
            field = value
            binding.tvAnswer.setText(value)
        }

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.FAQView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                question = getString(R.styleable.FAQView_question)
                answer = getString(R.styleable.FAQView_answer)
            } finally {
                recycle()
            }
        }
    }
}