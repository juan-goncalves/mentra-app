package me.juangoncalves.pie.labeled

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import me.juangoncalves.pie.PiePortion
import me.juangoncalves.pie.R
import me.juangoncalves.pie.extensions.closeTo
import me.juangoncalves.pie.extensions.toRadians
import me.juangoncalves.pie.unlabeled.PiePortionPosition
import me.juangoncalves.pie.unlabeled.PiePortionValidator
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PieChartViewV2(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val viewContainer = RectF()
    private val pieChartContainer = RectF()
    private val defaultPaint = generatePiecePaint(Color.CYAN, Color.GRAY)

    private var portions: Array<PiePortion> = emptyArray()
    private var anglesForPortions: Array<PiePortionPosition> = emptyArray()
    private var arcCenters: HashMap<PiePortionPosition, Pair<Float, Float>> = hashMapOf()
    private var paintsForPortions: Map<PiePortion, Paint> = emptyMap()
    private val portionValidator = PiePortionValidator()

    var colors: IntArray? = null

    fun setPortions(portions: Array<PiePortion>) {
        portionValidator.validatePortions(portions)
        Arrays.sort(portions, Collections.reverseOrder())
        anglesForPortions = calculateArcsForPortions(portions)
        paintsForPortions = selectPaintsForPortions(portions)
        updateArcCenters()
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
            val padding = if (portion.percentage closeTo 1.0) 0 else 8
            portionPositions += PiePortionPosition(startAngle + padding, sweepAngle - padding)
            startAngle += sweepAngle
        }
        return portionPositions.toTypedArray()
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

            val (arcCenterX, arcCenterY) = arcCenters[angles] ?: continue
            canvas.drawCircle(arcCenterX, arcCenterY, 5f, defaultPaint)
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

        viewContainer.left = paddingStart.toFloat()
        viewContainer.right = width - paddingEnd.toFloat()
        viewContainer.top = paddingTop.toFloat()
        viewContainer.bottom = height - paddingBottom.toFloat()

        val diameter = min(usableWidth, usableHeight) * PieSizePercentage
        val radius = diameter / 2f

        pieChartContainer.left = 0f
        pieChartContainer.right = diameter

        val verticalCenter = diameter / 2
        pieChartContainer.top = verticalCenter - radius
        pieChartContainer.bottom = verticalCenter + radius

        pieChartContainer.offsetTo(
            viewContainer.centerX() - radius,
            viewContainer.centerY() - radius
        )

        updateArcCenters()
    }

    /** Recalculate the center coordinates of each portion arc */
    private fun updateArcCenters() {
        arcCenters.clear()
        anglesForPortions.forEach { arcCenters[it] = calculateArcCenter(it) }
    }

    private val usableWidth get() = width - paddingHorizontal
    private val usableHeight get() = height - paddingVertical

    private val paddingHorizontal get() = paddingStart + paddingEnd
    private val paddingVertical get() = paddingTop + paddingBottom

    private val pieDiameter get() = pieChartContainer.width() - paddingEnd - paddingStart
    private val pieRadius get() = pieDiameter / 2f

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

    private fun calculateArcCenter(angles: PiePortionPosition): Pair<Float, Float> {
        val endAngle = angles.startAngle + angles.sweepAngle
        val middleAngle = ((angles.startAngle + endAngle) / 2).toRadians()
        val rx = pieChartContainer.centerX() + pieRadius * cos(middleAngle)
        val ry = pieChartContainer.centerY() + pieRadius * sin(middleAngle)
        return Pair(rx.toFloat(), ry.toFloat())
    }

    companion object {
        private const val PIE_RADIUS = 230
        private const val PIE_DIAMETER = PIE_RADIUS * 2
        const val PORTION_THRESHOLD = 0.01

        // Amount of space to take to draw the pie chart, leaving the rest to display the labels
        const val PieSizePercentage = 0.75f
    }

}