package com.github.kr328.clash.design.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.bumptech.glide.Glide
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.databinding.ComponentActionLabelBinding
import com.github.kr328.clash.design.databinding.ComponentCellBinding
import com.github.kr328.clash.design.util.layoutInflater

class CellView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : RelativeLayout(context, attributeSet, defStyleAttr, defStyleRes) {
    private val binding = ComponentCellBinding
        .inflate(context.layoutInflater, this, true)

    var iconUrl: CharSequence? = null
        get() {
            return field
        }
        set(value) {
            field = value
            if (TextUtils.isEmpty(field)) {
                binding.ivIcon.visibility = GONE
            } else {
                binding.ivIcon.visibility = VISIBLE
                Glide.with(context)
                    .load(field)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.ivIcon)
            }
        }

    var label: CharSequence?
        get() = binding.tvLabel.text
        set(value) {
            binding.tvLabel.text = value
        }

    var value: CharSequence?
        get() = binding.tvValue.text
        set(value) {
            binding.tvValue.text = value
            binding.tvValue.visibility = if (value == null) View.GONE else View.VISIBLE
        }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.root.setOnClickListener(l)
    }

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.CellView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                iconUrl = getString(R.styleable.CellView_iconUrl)
                label = getString(R.styleable.CellView_label)
                value = getString(R.styleable.CellView_value)
            } finally {
                recycle()
            }
        }
    }
}