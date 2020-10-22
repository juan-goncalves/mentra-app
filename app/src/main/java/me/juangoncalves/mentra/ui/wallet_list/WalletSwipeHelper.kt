package me.juangoncalves.mentra.ui.wallet_list

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.math.MathUtils
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.extensions.getThemeColor
import java.lang.ref.WeakReference
import kotlin.math.abs

class WalletTouchHelper(context: Context, listener: WalletSwipeHelper.Listener) :
    ItemTouchHelper(WalletSwipeHelper(context, listener))

class WalletSwipeHelper(context: Context, listener: Listener) : ItemTouchHelper.Callback() {

    interface Listener {
        fun onDeleteWalletGesture(position: Int)
        fun onEditWalletGesture(position: Int)
    }

    private val listener: WeakReference<Listener> = WeakReference(listener)
    private val deleteDrawable: Drawable? = getDrawable(context, R.drawable.ic_trash)
    private val deleteIconColor: Int = context.getThemeColor(R.attr.errorIconTint)
    private val editDrawable: Drawable? = getDrawable(context, R.drawable.ic_edit)
    private val editIconColor: Int = context.getThemeColor(R.attr.colorOnWarning)
    private val intrinsicWidth: Int = deleteDrawable?.intrinsicWidth ?: 0
    private val intrinsicHeight: Int = deleteDrawable?.intrinsicHeight ?: 0

    private val errorPaint: Paint = Paint().apply {
        color = context.getThemeColor(R.attr.colorError)
        isAntiAlias = true
        setShadowLayer(8f, 0.0f, 0.0f, color)
    }

    private val warningPaint: Paint = Paint().apply {
        color = context.getThemeColor(R.attr.colorWarning)
        isAntiAlias = true
        setShadowLayer(8f, 0.0f, 0.0f, color)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.2f

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.RIGHT ->
                listener.get()?.onEditWalletGesture(viewHolder.adapterPosition)
            ItemTouchHelper.LEFT ->
                listener.get()?.onDeleteWalletGesture(viewHolder.adapterPosition)
        }
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
        val (rect, bubblePaint, iconDrawable, iconColor) = generateIconProperties(
            recyclerView,
            viewHolder.itemView,
            dX
        )

        canvas.drawCircle(
            rect.centerX().toFloat(),
            rect.centerY().toFloat(),
            rect.width() / 2f + rect.width() * 0.4f,
            bubblePaint
        )

        iconDrawable?.apply {
            bounds = rect
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                iconColor,
                BlendModeCompat.SRC_IN
            )
            draw(canvas)
        }

        val leftMax = recyclerView.width * -0.185f
        val rightMax = leftMax * -1

        super.onChildDraw(
            canvas,
            recyclerView,
            viewHolder,
            MathUtils.clamp(dX, leftMax, rightMax),
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun generateIconProperties(
        recyclerView: RecyclerView,
        itemView: View,
        dX: Float
    ): IconProps {
        val (scaledHeight, scaledWidth) = scaleDimensionsByCurrentSwipe(dX, recyclerView)

        return IconProps(
            generateIconRect(itemView, dX, scaledHeight, scaledWidth),
            if (dX > 0) warningPaint else errorPaint,
            if (dX > 0) editDrawable else deleteDrawable,
            if (dX > 0) editIconColor else deleteIconColor
        )
    }

    private fun scaleDimensionsByCurrentSwipe(
        dX: Float,
        recyclerView: RecyclerView
    ): Pair<Float, Float> {
        val swipePercentage = abs(dX / recyclerView.width)
        val sizePercentage = MathUtils.clamp(swipePercentage / 0.15f, 0f, 1f)
        val scaledHeight = intrinsicHeight * sizePercentage
        val scaledWidth = intrinsicWidth * sizePercentage
        return Pair(scaledHeight, scaledWidth)
    }

    private fun generateIconRect(
        itemView: View,
        dX: Float,
        scaledHeight: Float,
        scaledWidth: Float
    ): Rect {
        val margin = (itemView.height - scaledHeight) / 4
        val iconTop = itemView.top + (itemView.height - scaledHeight) / 2
        val iconBottom = iconTop + scaledHeight

        val iconLeft = if (dX > 0) {
            itemView.left + margin
        } else {
            itemView.right - margin - scaledWidth
        }

        val iconRight = if (dX > 0) {
            itemView.left + margin + scaledWidth
        } else {
            itemView.right - margin
        }

        return Rect(iconLeft.toInt(), iconTop.toInt(), iconRight.toInt(), iconBottom.toInt())
    }

    private data class IconProps(
        val position: Rect,
        val paint: Paint,
        val iconDrawable: Drawable?,
        @ColorInt val color: Int
    )

}