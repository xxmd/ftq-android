import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

object ViewHelper {

    // 为 View 添加卡片效果，并支持背景颜色和阴影颜色配置
    fun addCardEffect(
        view: View,
        cornerRadius: Float = 16f,    // 圆角半径
        elevation: Float = 10f,       // 阴影强度
        backgroundColor: Int = Color.WHITE,   // 背景颜色（默认为白色）
        shadowColor: Int = Color.GRAY      // 阴影颜色（默认为黑色）
    ) {
        if (view is CardView) {
            // 如果是 CardView，直接通过 CardView 的属性设置效果
            view.radius = cornerRadius
            view.cardElevation = elevation
            view.setCardBackgroundColor(backgroundColor)
            return
        }

        // 为其他 View 创建自定义背景 drawable
        val background = createCardBackground(view.context, cornerRadius, elevation, backgroundColor, shadowColor)

        // 为 View 设置背景
        ViewCompat.setBackground(view, background)

        // 如果视图支持 elevation（例如 API 21+），设置 elevation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = elevation
        }
    }

    // 创建卡片背景（包含圆角和阴影，支持自定义颜色）
    private fun createCardBackground(
        context: Context,
        cornerRadius: Float,
        elevation: Float,
        backgroundColor: Int,
        shadowColor: Int
    ): Drawable {
        // 创建一个 GradientDrawable 来模拟圆角效果
        val shapeDrawable = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            this.cornerRadius = cornerRadius
        }

        // 如果 API >= 21，使用 View 的 elevation 来自动处理阴影
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // 对于 API 21 以下版本，不能直接使用 elevation，所以我们需要模拟阴影
//            shapeDrawable.setShadowLayer(elevation, 0f, 4f, ContextCompat.getColor(context, shadowColor))
        }

        return shapeDrawable
    }
}
