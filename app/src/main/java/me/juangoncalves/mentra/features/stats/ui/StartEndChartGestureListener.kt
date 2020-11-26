package me.juangoncalves.mentra.features.stats.ui

import android.view.MotionEvent
import com.github.mikephil.charting.listener.OnChartGestureListener
import me.juangoncalves.mentra.extensions.empty

/** Helper class to avoid overriding a bunch of methods that we don't care about */
abstract class StartEndChartGestureListener : OnChartGestureListener {

    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) = empty()

    override fun onChartLongPressed(me: MotionEvent?) = empty()
    override fun onChartDoubleTapped(me: MotionEvent?) = empty()
    override fun onChartSingleTapped(me: MotionEvent?) = empty()
    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) = empty()
    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) = empty()

}