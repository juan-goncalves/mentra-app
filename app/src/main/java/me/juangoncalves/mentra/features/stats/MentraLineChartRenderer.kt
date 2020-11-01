package me.juangoncalves.mentra.features.stats

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorInt
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class MentraLineChartRenderer(
    @ColorInt backgroundColor: Int,
    @ColorInt private val textColor: Int,
    chart: LineDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineChartRenderer(chart, animator, viewPortHandler) {

    companion object {
        private const val PADDING = 10f
        private const val CORNER_RADIUS = 15f
    }

    private val backgroundPaint = Paint().apply {
        color = backgroundColor
        alpha = 180
        isAntiAlias = true
    }

    override fun drawValue(c: Canvas?, valueText: String?, x: Float, y: Float, color: Int) {
        if (c == null || valueText == null) return

        val actualY = y - 5 // Draw the value a little bit higher
        mValuePaint.color = textColor

        val (textWidth, textHeight) = calculateTextDimensions(valueText)

        c.drawRoundRect(
            x - textWidth / 2 - PADDING,
            actualY + PADDING,
            x + textWidth / 2 + PADDING,
            actualY - textHeight - PADDING / 2,
            CORNER_RADIUS,
            CORNER_RADIUS,
            backgroundPaint
        )

        c.drawText(valueText, x, actualY, mValuePaint)
    }

    private fun calculateTextDimensions(valueText: String): Pair<Float, Int> {
        val rect = Rect()
        mValuePaint.getTextBounds(valueText, 0, 1, rect)
        val textWidth = mValuePaint.measureText(valueText)
        val textHeight = rect.height()
        return Pair(textWidth, textHeight)
    }

}