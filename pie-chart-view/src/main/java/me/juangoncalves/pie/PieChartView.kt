package me.juangoncalves.pie

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import me.juangoncalves.pie.domain.GridZone
import me.juangoncalves.pie.domain.PiePortionValidator
import me.juangoncalves.pie.domain.PortionDrawData
import me.juangoncalves.pie.extensions.asPercentage
import me.juangoncalves.pie.extensions.closeTo
import me.juangoncalves.pie.extensions.toRadians
import java.util.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin


class PieChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    var colors: IntArray? = null

    var showPercentages: Boolean = true
        set(value) {
            field = value
            portionsDrawData = calculatePieDrawData(piePortions)
            invalidate()
        }

    private val viewContainer = RectF()
    private val pieChartContainer = RectF()

    private val defaultPaint = generatePiecePaint(Color.CYAN, Color.GRAY)
    private val linePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        strokeWidth = 3f
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

    private val textHorizontalMargin = 5f * resources.displayMetrics.density
    private val usableWidth get() = width - paddingHorizontal
    private val usableHeight get() = height - paddingVertical
    private val paddingHorizontal get() = paddingStart + paddingEnd
    private val paddingVertical get() = paddingTop + paddingBottom
    private val pieDiameter get() = pieChartContainer.width()
    private val pieRadius get() = pieDiameter / 2f
    private val defaultLabelWidth = textPaint.measureText("NANO (99.9%)")
    private val horizontalLabelsSpace = (defaultLabelWidth + DefaultTextLineLength).toInt() * 2
    private val verticalLabelSpace = DefaultTextLineLength.toInt() * 2

    init {
        processAttributes(context, attrs)

        // Add some sample portions to display the chart in the XML previews
        if (isInEditMode) {
            setPortions(PreviewPortions)
        }
    }

    private fun processAttributes(context: Context, attrs: AttributeSet?) {
        with(context.obtainStyledAttributes(attrs, R.styleable.PieChartView)) {
            linePaint.color = getColor(
                R.styleable.PieChartView_pie_text_line_color,
                ResourcesCompat.getColor(context.resources, R.color.black, context.theme)
            )

            textPaint.color = getColor(
                R.styleable.PieChartView_pie_label_text_color,
                ResourcesCompat.getColor(context.resources, R.color.black, context.theme)
            )

            textPaint.textSize = getDimension(
                R.styleable.PieChartView_pie_label_text_size,
                14 * resources.displayMetrics.density
            )

            showPercentages = getBoolean(R.styleable.PieChartView_pie_label_show_percentage, true)

            val colorArrayId = getResourceId(
                R.styleable.PieChartView_pie_colors,
                R.array.defaultPieColorPairs
            )

            colors = resources.getIntArray(colorArrayId)

            recycle()
        }
    }

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

            if (portion.percentage >= PortionThreshold) {
                drawPortionArc(canvas, startAngle, sweepAngle, arcPaint)
                drawTextLine(canvas, textLineStartPoint, textLineMiddlePoint, textLineEndPoint)
                drawText(canvas, textLayoutPosition, textLayout)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = DefaultPieDiameter + paddingStart + paddingEnd + horizontalLabelsSpace
        val desiredHeight = DefaultPieDiameter + paddingTop + paddingBottom + verticalLabelSpace

        val measuredWidth = measureDimension(desiredWidth, widthMeasureSpec)
        val measuredHeight = measureDimension(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> desiredSize.coerceAtMost(specSize)
            MeasureSpec.UNSPECIFIED -> desiredSize
            else -> desiredSize
        }
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
            ArcStrokeWidth / 4,
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

    /**
     * Create a Paint object with the style for the Pie portions, using a
     * gradient between the received colors.
     */
    private fun generatePiecePaint(@ColorInt colorFrom: Int, @ColorInt colorTo: Int): Paint {
        return Paint().apply {
            style = Paint.Style.STROKE
            color = colorFrom
            strokeWidth = ArcStrokeWidth
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

    private fun calculatePieDrawData(portions: Array<PiePortion>): Array<PortionDrawData> {
        val result = arrayListOf<PortionDrawData>()
        var currStartAngle = 30.0

        portions.forEach { portion ->
            val usedSweepAngle = 360 * portion.percentage

            val padding = if (portion.percentage closeTo 1.0) 0 else 8
            val startAngle = currStartAngle + padding
            val sweepAngle = usedSweepAngle - padding

            val (tStart, tMiddle, tEnd) = calculateTextLineBreakPoints(startAngle, sweepAngle)
            val (textLayout, textLayoutPosition) = textLayoutForPortion(tStart, tEnd, portion)

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

    private fun calculateTextLineBreakPoints(
        startAngle: Double,
        sweepAngle: Double
    ): Triple<PointF, PointF, PointF> {
        val endAngle = startAngle + sweepAngle
        val middleAngle = ((startAngle + endAngle) / 2).toRadians()
        val rx = pieChartContainer.centerX() + pieRadius * cos(middleAngle)
        val ry = pieChartContainer.centerY() + pieRadius * sin(middleAngle)
        val arcCenter = PointF(rx.toFloat(), ry.toFloat())
        val lineAngle = getArcZone(arcCenter).textLineDegree()

        val middle = when {
            lineAngle closeTo 0.0 -> arcCenter
            else -> {
                val textLineLength = pieRadius * 0.3
                val sx = rx + textLineLength * cos(lineAngle)
                val sy = ry + textLineLength * sin(lineAngle)
                PointF(sx.toFloat(), sy.toFloat())
            }
        }

        val ex = pieRadius * 0.3f * if (arcCenter.x < pieChartContainer.centerX()) -1 else 1
        val end = PointF(middle.x + ex, middle.y)

        return Triple(arcCenter, middle, end)
    }

    private fun textLayoutForPortion(
        tStart: PointF,
        tEnd: PointF,
        portion: PiePortion
    ): Pair<StaticLayout, PointF> {
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

        val text = when {
            showPercentages -> "${portion.text} (${portion.percentage.asPercentage()})"
            else -> portion.text
        }

        val textLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, max(textLayoutMaxWidth, 0))
            .setAlignment(textLayoutAlignment)
            .setMaxLines(1)
            .build()

        val halfCompensation = if (isInLeftHalf) textLayout.width else 0
        val textLayoutX = tEnd.x + textHorizontalSpace - halfCompensation
        val textLayoutPosition = PointF(textLayoutX, tEnd.y - textLayout.height / 2)
        return Pair(textLayout, textLayoutPosition)
    }

    /** Get the [GridZone] in which the [point] is contained. */
    private fun getArcZone(point: PointF): GridZone {
        val third = pieDiameter / 3f
        val verticalBreakpoint1 = pieChartContainer.left + third
        val verticalBreakpoint2 = verticalBreakpoint1 + third
        val horizontalBreakpoint1 = pieChartContainer.top + third
        val horizontalBreakpoint2 = horizontalBreakpoint1 + third

        val isInFirstRow = point.y <= horizontalBreakpoint1
        val isInMiddleRow = point.y in horizontalBreakpoint1..horizontalBreakpoint2
        val isInEndRow = point.y >= horizontalBreakpoint2

        val isInFirstCol = point.x <= verticalBreakpoint1
        val isInMiddleCol = point.x in verticalBreakpoint1..verticalBreakpoint2
        val isInEndCol = point.x >= verticalBreakpoint2

        return when {
            isInEndCol && isInFirstRow -> GridZone.TopRight
            isInEndCol && isInMiddleRow -> GridZone.MiddleRight
            isInEndCol && isInEndRow -> GridZone.BottomRight
            isInMiddleCol && isInFirstRow -> GridZone.TopMiddle
            isInMiddleCol && isInEndRow -> GridZone.BottomMiddle
            isInFirstCol && isInFirstRow -> GridZone.TopLeft
            isInFirstCol && isInMiddleRow -> GridZone.MiddleLeft
            isInFirstCol && isInEndRow -> GridZone.BottomLeft
            else -> GridZone.None
        }
    }

    companion object {
        private const val DefaultPieRadius = 230
        private const val DefaultPieDiameter = DefaultPieRadius * 2
        private const val ArcStrokeWidth = 16f
        private const val DefaultTextLineLength = DefaultPieRadius * 0.3

        // Amount of space to take to draw the pie chart, leaving the rest to display the labels
        private const val PieSizePercentage = 0.70f

        // Minimum portion size
        internal const val PortionThreshold = 0.01

        private val PreviewPortions = arrayOf(
            PiePortion(0.70, "BTC"),
            PiePortion(0.20, "ETH"),
            PiePortion(0.10, "NANO")
        )
    }

}