package me.juangoncalves.pie.domain

import android.graphics.Paint
import android.graphics.PointF
import android.text.StaticLayout
import me.juangoncalves.pie.PiePortion

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
