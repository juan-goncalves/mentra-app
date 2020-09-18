package me.juangoncalves.mentra.ui.wallet_list

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.math.MathUtils
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.extensions.getThemeColor
import kotlin.math.abs

class SwipeToDeleteHelper(
    context: Context,
    private val onDelete: (Int) -> Unit
) : ItemTouchHelper.Callback() {

    private val deleteDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_trash)
    private val intrinsicWidth: Int = deleteDrawable?.intrinsicWidth ?: 0
    private val intrinsicHeight: Int = deleteDrawable?.intrinsicHeight ?: 0
    private val colorOnError: Int = context.getThemeColor(R.attr.colorOnError)
    private val errorPaint: Paint = Paint().apply {
        color = context.getThemeColor(R.attr.colorError)
        isAntiAlias = true
        setShadowLayer(8f, 0.0f, 0.0f, color)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.7f

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onDelete(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val (scaledHeight, scaledWidth) = scaleDimensionsByCurrentSwipe(dX, recyclerView)
        val rect = generateIconRect(itemView, scaledHeight, scaledWidth)

        canvas.drawCircle(
            rect.centerX().toFloat(),
            rect.centerY().toFloat(),
            scaledWidth / 2f + scaledWidth * 0.4f,
            errorPaint
        )

        deleteDrawable?.apply {
            bounds = rect
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                colorOnError,
                BlendModeCompat.SRC_IN
            )
            draw(canvas)
        }
    }

    private fun scaleDimensionsByCurrentSwipe(
        dX: Float,
        recyclerView: RecyclerView
    ): Pair<Int, Int> {
        val swipePercentage = abs(dX / recyclerView.width)
        val sizePercentage = MathUtils.clamp(swipePercentage / 0.15f, 0f, 1f)
        val scaledHeight = (intrinsicHeight * sizePercentage).toInt()
        val scaledWidth = (intrinsicWidth * sizePercentage).toInt()
        return Pair(scaledHeight, scaledWidth)
    }

    private fun generateIconRect(
        itemView: View,
        scaledHeight: Int,
        scaledWidth: Int
    ): Rect {
        val margin = (itemView.height - scaledHeight) / 4
        val iconTop = itemView.top + (itemView.height - scaledHeight) / 2
        val iconLeft = itemView.right - margin - scaledWidth
        val iconRight = itemView.right - margin
        val iconBottom = iconTop + scaledHeight
        return Rect(iconLeft, iconTop, iconRight, iconBottom)
    }

}