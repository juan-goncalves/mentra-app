package me.juangoncalves.pie.labeled

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.labeled_pie_chart_view.view.*
import me.juangoncalves.pie.PieManager
import me.juangoncalves.pie.PiePortion
import me.juangoncalves.pie.R

class LabeledPieChartView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val labelAdapter: LabelAdapter
    private val pieManager = PieManager()

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.labeled_pie_chart_view, this, true)
        labelAdapter = LabelAdapter()
        pieChartLabelList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = labelAdapter
            setHasFixedSize(true)
        }
    }

    @Throws(IllegalArgumentException::class)
    fun setPortions(portions: Array<PiePortion>, accumulatedPortionText: String = "Other") {
        val reducedPortions = pieManager.reducePortions(portions, accumulatedPortionText)
        pieChartView.setPortions(reducedPortions)
        val labelItems =
            reducedPortions.map { LabelItem(it.text, pieChartView.getColorForPortions(it)) }
        labelAdapter.updateDataSet(labelItems.toTypedArray())
    }

}