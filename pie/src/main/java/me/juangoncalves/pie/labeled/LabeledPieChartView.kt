package me.juangoncalves.pie.labeled

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.labeled_pie_chart_view.view.*
import me.juangoncalves.pie.PieManager
import me.juangoncalves.pie.PiePortion
import me.juangoncalves.pie.R
import me.juangoncalves.pie.extensions.asPercentage
import me.juangoncalves.pie.unlabeled.PieChartView

class LabeledPieChartView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val labelAdapter: LabelAdapter
    private val pieManager = PieManager()

    @ColorInt
    var hiddenLabelColor: Int = 0

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.labeled_pie_chart_view, this, true)
        labelAdapter = LabelAdapter()
        pieChartLabelList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = labelAdapter
            setHasFixedSize(true)
        }
        processAttributes(context, attrs)
    }

    @Throws(IllegalArgumentException::class)
    fun setPortions(portions: Array<PiePortion>, accumulatedPortionText: String = "Other") {
        val reducedPortions = pieManager.reducePortions(portions, accumulatedPortionText)
        pieChartView.setPortions(reducedPortions)
        val labelItems = reducedPortions.map { portion ->
            val color = if (portion.percentage < PieChartView.PORTION_THRESHOLD) {
                hiddenLabelColor
            } else {
                pieChartView.getPortionColor(portion)
            }
            LabelItem(
                "${portion.text} (${portion.percentage.asPercentage()})",
                color
            )
        }
        labelAdapter.updateDataSet(labelItems.toTypedArray())
    }

    private fun processAttributes(context: Context, attrs: AttributeSet?) {
        with(context.obtainStyledAttributes(attrs, R.styleable.LabeledPieChartView)) {
            hiddenLabelColor = getColor(
                R.styleable.LabeledPieChartView_pie_hiddenLabelColor,
                ResourcesCompat.getColor(context.resources, R.color.gray, context.theme)
            )

            val colorArrayId = getResourceId(
                R.styleable.LabeledPieChartView_pie_colors,
                R.array.defaultPieColorPairs
            )
            pieChartView.colors = resources.getIntArray(colorArrayId)

            recycle()
        }
    }

}