package me.juangoncalves.pie.unlabeled

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import me.juangoncalves.pie.PiePortion
import me.juangoncalves.pie.R
import java.util.*

internal class PieChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val pieChartContainer = RectF()
    private val defaultPaint = generatePiecePaint(Color.CYAN, Color.GRAY)

    private var portions: Array<PiePortion> = emptyArray()
    private var anglesForPortions: Array<PiePortionPosition> = emptyArray()
    private var paintsForPortions: Map<PiePortion, Paint> = emptyMap()
    private val portionValidator = PiePortionValidator()

    var colors: IntArray? = null

    fun setPortions(portions: Array<PiePortion>) {
        portionValidator.validatePortions(portions)
        Arrays.sort(portions, Collections.reverseOrder())
        anglesForPortions = calculateArcsForPortions(portions)
        paintsForPortions = selectPaintsForPortions(portions)
        this.portions = portions
        invalidate()
    }

    private fun selectPaintsForPortions(portions: Array<PiePortion>): Map<PiePortion, Paint> {
        val piecePaintMap = hashMapOf<PiePortion, Paint>()
        val portionColors = colors ?: context.resources.getIntArray(R.array.defaultPieColorPairs)
        check(portionColors.size % 2 == 0) {
            // This is a programming error caused by developers, this shouldn't be thrown in a production release
            "The defaultPieColorPairs array has to have an even amount of items."
        }
        var colorIndex = 0
        for (piece in portions) {
            val colorFrom = portionColors[colorIndex++]
            val colorTo = portionColors[colorIndex++]
            val paint = generatePiecePaint(colorFrom, colorTo)
            piecePaintMap[piece] = paint
            if (colorIndex + 1 >= portionColors.size) {
                colorIndex = 0
            }
        }
        return piecePaintMap
    }

    private fun calculateArcsForPortions(portions: Array<PiePortion>): Array<PiePortionPosition> {
        var startAngle = 30.0
        val portionPositions = arrayListOf<PiePortionPosition>()
        portions.forEach { portion ->
            val sweepAngle = 360 * portion.percentage
            portionPositions += PiePortionPosition(startAngle + 8, sweepAngle - 8)
            startAngle += sweepAngle
        }
        return portionPositions.toTypedArray()
    }

    @ColorInt
    fun getPortionColor(portion: PiePortion): Int {
        val paint = paintsForPortions[portion]
            ?: throw IllegalArgumentException("PiePortion not present in the PieChart")

        return paint.color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in portions.indices) {
            val angles = anglesForPortions[i]
            val currentPortionPaint = paintsForPortions[portions[i]]
            if (portions[i].percentage < PORTION_THRESHOLD) continue
            canvas.drawArc(
                pieChartContainer,
                angles.startAngle.toFloat(),
                angles.sweepAngle.toFloat(),
                false,
                currentPortionPaint ?: defaultPaint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val desiredWidth = PIE_DIAMETER + paddingStart + paddingEnd
        val desiredHeight = PIE_DIAMETER + paddingTop + paddingBottom
        val width: Int
        width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> desiredWidth.coerceAtMost(widthSize)
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> desiredWidth
        }
        val height: Int
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> desiredHeight.coerceAtMost(heightSize)
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> desiredHeight
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        pieChartContainer.left = paddingStart.toFloat()
        pieChartContainer.right = pieChartContainer.left + PIE_DIAMETER
        val verticalCenter = height / 2
        pieChartContainer.top = verticalCenter - PIE_RADIUS.toFloat()
        pieChartContainer.bottom = verticalCenter + PIE_RADIUS.toFloat()
    }

    /**
     * Create a Paint object with the style for the Pie portions, using a
     * gradient between the received colors.
     */
    private fun generatePiecePaint(@ColorInt colorFrom: Int, @ColorInt colorTo: Int): Paint {
        return Paint().apply {
            style = Paint.Style.STROKE
            color = colorFrom
            strokeWidth = 16f
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            shader = LinearGradient(
                pieChartContainer.left,
                pieChartContainer.bottom,
                pieChartContainer.right,
                pieChartContainer.top,
                colorFrom,
                colorTo,
                Shader.TileMode.MIRROR
            )
        }
    }

    companion object {
        private const val PIE_RADIUS = 200
        private const val PIE_DIAMETER = PIE_RADIUS * 2
        const val PORTION_THRESHOLD = 0.01
    }

}