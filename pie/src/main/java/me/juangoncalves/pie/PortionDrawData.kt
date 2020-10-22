package me.juangoncalves.pie

import android.graphics.Paint
import android.graphics.PointF
import android.text.StaticLayout

internal data class PortionDrawData(
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
