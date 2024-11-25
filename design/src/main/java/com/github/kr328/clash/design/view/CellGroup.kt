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
            } finally {
                recycle()
            }
        }
    }
}