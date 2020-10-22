package me.juangoncalves.pie.labeled

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import me.juangoncalves.pie.PiePortion
import me.juangoncalves.pie.R
import me.juangoncalves.pie.extensions.closeTo
import me.juangoncalves.pie.extensions.toRadians
import me.juangoncalves.pie.unlabeled.PiePortionValidator
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class PieChartViewV2(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val textHorizontalMargin = 8f * resources.displayMetrics.density

    private val viewContainer = RectF()
    private val pieChartContainer = RectF()
    private val defaultPaint = generatePiecePaint(Color.CYAN, Color.GRAY)
    private val linePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textSize = 14 * resources.displayMetrics.density
    }

    private var piePortions: Array<PiePortion> = emptyArray()
    private var portionsDrawData: Array<PortionDrawData> = emptyArray()
    private var paintsForPortions: Map<PiePortion, Paint> = emptyMap()
    private val portionValidator = PiePortionValidator()

    var colors: IntArray? = null

    fun setPortions(portions: Array<PiePortion>) {
        portionValidator.validatePortions(portions)
        Arrays.sort(portions, Collections.reverseOrder())
        paintsForPortions = selectPaintsForPortions(portions)
        portionsDrawData = calculatePieDrawData(portions)
        piePortions = portions
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

    data class PortionDrawData(
        val portion: PiePortion,
        val arcPaint: Paint?,
        val startAngle: Double,
        val sweepAngle: Double,
        val textLineStartPoint: PointF,
        val textLineMiddlePoint: PointF,
        val textLineEndPoint: PointF,
        val textLayoutPosition: PointF,
        val textLayout: StaticLayout
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        portionsDrawData.forEach { portionData ->
            val (
                portion,
                arcPaint,
                startAngle,
                sweepAngle,
                textLineStartPoint,
                textLineMiddlePoint,
                textLineEndPoint,
                textLayoutPosition,
                textLayout
            ) = portionData

            if (portion.percentage >= PORTION_THRESHOLD) {
                drawPortionArc(canvas, startAngle, sweepAngle, arcPaint)
                drawTextLine(canvas, textLineStartPoint, textLineMiddlePoint, textLineEndPoint)
                drawText(canvas, textLayoutPosition, textLayout)
            }
        }
    }

    private fun drawPortionArc(
        canvas: Canvas,
        startAngle: Double,
        sweepAngle: Double,
        arcPaint: Paint?
    ) {
        canvas.drawArc(
            pieChartContainer,
            startAngle.toFloat(),
            sweepAngle.toFloat(),
            false,
            arcPaint ?: defaultPaint
        )
    }

    private fun drawText(
        canvas: Canvas,
        textLayoutPosition: PointF,
        textLayout: StaticLayout
    ) {
        canvas.save()
        canvas.translate(textLayoutPosition.x, textLayoutPosition.y)
        textLayout.draw(canvas)
        canvas.restore()
    }

    private fun drawTextLine(
        canvas: Canvas,
        textLineStartPoint: PointF,
        textLineMiddlePoint: PointF,
        textLineEndPoint: PointF
    ) {
        canvas.drawCircle(
            textLineStartPoint.x,
            textLineStartPoint.y,
            PortionStrokeWidth / 3,
            linePaint
        )

        canvas.drawLine(
            textLineStartPoint.x,
            textLineStartPoint.y,
            textLineMiddlePoint.x,
            textLineMiddlePoint.y,
            linePaint
        )

        canvas.drawLine(
            textLineMiddlePoint.x,
            textLineMiddlePoint.y,
            textLineEndPoint.x,
            textLineEndPoint.y,
            linePaint
        )
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

        portionsDrawData = calculatePieDrawData(piePortions)
    }

    private fun calculatePieDrawData(portions: Array<PiePortion>): Array<PortionDrawData> {
        val result = arrayListOf<PortionDrawData>()
        var currStartAngle = 30.0

        portions.forEach { portion ->
            val usedSweepAngle = 360 * portion.percentage

            val padding = if (portion.percentage closeTo 1.0) 0 else 8
            val startAngle = currStartAngle + padding
            val sweepAngle = usedSweepAngle - padding

            val (tStart, tMiddle, tEnd) = calculateTextLineBreakPoints(startAngle, sweepAngle)

            val isInLeftHalf = tStart.x < pieChartContainer.centerX()
            val textHorizontalSpace = textHorizontalMargin * if (isInLeftHalf) -1 else 1

            val textLayoutMaxWidth = when {
                isInLeftHalf -> tEnd.x - viewContainer.left
                else -> viewContainer.right - tEnd.x
            }.toInt()

            val textLayoutAlignment = when {
                isInLeftHalf -> Layout.Alignment.ALIGN_OPPOSITE
                else -> Layout.Alignment.ALIGN_NORMAL
            }

            val textLayout = StaticLayout.Builder
                .obtain(portion.text, 0, portion.text.length, textPaint, textLayoutMaxWidth)
                .setAlignment(textLayoutAlignment)
                .setMaxLines(1)
                .build()

            val halfCompensation = if (isInLeftHalf) textLayout.width else 0
            val textLayoutX = tEnd.x + textHorizontalSpace - halfCompensation
            val textLayoutPosition = PointF(textLayoutX, tEnd.y - textLayout.height / 2)

            result += PortionDrawData(
                portion = portion,
                arcPaint = paintsForPortions[portion],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                textLineStartPoint = tStart,
                textLineMiddlePoint = tMiddle,
                textLineEndPoint = tEnd,
                textLayoutPosition = textLayoutPosition,
                textLayout = textLayout
            )

            currStartAngle += usedSweepAngle
        }

        return result.toTypedArray()
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
            strokeWidth = PortionStrokeWidth
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

    private fun calculateTextLineBreakPoints(
        startAngle: Double,
        sweepAngle: Double
    ): Triple<PointF, PointF, PointF> {
        val endAngle = startAngle + sweepAngle
        val middleAngle = ((startAngle + endAngle) / 2).toRadians()
        val rx = pieChartContainer.centerX() + (pieRadius + PortionStrokeWidth) * cos(middleAngle)
        val ry = pieChartContainer.centerY() + (pieRadius + PortionStrokeWidth) * sin(middleAngle)
        val arcCenter = PointF(rx.toFloat(), ry.toFloat())

        val textLineLength = pieRadius * 0.3
        val sx = rx + textLineLength * cos(middleAngle + 45.0.toRadians())
        val sy = ry + textLineLength * sin(middleAngle + 45.0.toRadians())
        val middle = PointF(sx.toFloat(), sy.toFloat())

        val ex = pieRadius * 0.4f * if (arcCenter.x < pieChartContainer.centerX()) -1 else 1
        val endPoint = PointF(middle.x + ex, middle.y)

        return Triple(arcCenter, middle, endPoint)
    }

    companion object {
        private const val PIE_RADIUS = 230
        private const val PIE_DIAMETER = PIE_RADIUS * 2
        const val PORTION_THRESHOLD = 0.01

        // Amount of space to take to draw the pie chart, leaving the rest to display the labels
        const val PieSizePercentage = 0.70f

        const val PortionStrokeWidth = 16f
    }

}