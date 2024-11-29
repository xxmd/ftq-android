package com.github.kr328.clash.design.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import com.bumptech.glide.Glide
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.databinding.ComponentActionLabelBinding
import com.github.kr328.clash.design.databinding.ComponentCellBinding
import com.github.kr328.clash.design.util.layoutInflater

class CellGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    var title: CharSequence?
        get() = tvTitle.text
        set(value) {
            if (TextUtils.isEmpty(value)) {
                removeView(tvTitle)
            } else {
                addView(tvTitle, 0)
            }
            tvTitle.text = value
        }

    private lateinit var tvTitle: TextView

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.CellGroup,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                orientation = VERTICAL
                showDividers = SHOW_DIVIDER_MIDDLE
                dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider_cell_group)
                ViewHelper.addCardEffect(this@CellGroup)
                setPadding(60, 0, 60, 0)
                initTvTitle()
                title = getString(R.styleable.CellGroup_title)
            } finally {
                recycle()
            }
        }
    }

    private fun initTvTitle() {
        tvTitle = TextView(context)
        tvTitle.setTextSize(18f)
        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,   // 宽度
            LayoutParams.WRAP_CONTENT   // 高度
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL  // 或者 Gravity.CENTER
        layoutParams.setMargins(0, 30, 0, 30)
        tvTitle.layoutParams = layoutParams
    }
}