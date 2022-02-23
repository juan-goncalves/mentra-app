package me.juangoncalves.mentra.features.stats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.StatsFragmentBinding
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.extensions.getThemeColor
import me.juangoncalves.mentra.extensions.handleErrorsFrom
import me.juangoncalves.mentra.extensions.styleByTheme
import me.juangoncalves.mentra.extensions.updateVisibility
import me.juangoncalves.mentra.features.stats.model.StatsViewModel
import me.juangoncalves.mentra.features.stats.model.TimeChartData
import java.util.*


@AndroidEntryPoint
class StatsFragment : Fragment() {

    private val viewModel: StatsViewModel by viewModels()

    private var _binding: StatsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView()
        initObservers()
    }

    private fun configureView() {
        binding.dailyValueLineChart.applyDefaultStyle()
        binding.monthlyValueLineChart.applyDefaultStyle()
        binding.placeholderLineChart.applyPlaceholderStyle()

        binding.weeklyValueLineChart.applyDefaultStyle().apply {
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1.0f
            xAxis.labelCount = resources.getInteger(R.integer.weekly_chart_labels)
        }

        binding.statsRefreshLayout.styleByTheme().setOnRefreshListener {
            viewModel.refreshSelected()
        }

        binding.dailyValueChip.setOnCheckedChangeListener { _, selected ->
            if (selected) viewModel.timeGranularityChanged(TimeGranularity.Daily)
        }

        binding.weeklyValueChip.setOnCheckedChangeListener { _, selected ->
            if (selected) viewModel.timeGranularityChanged(TimeGranularity.Weekly)
        }

        binding.monthlyValueChip.setOnCheckedChangeListener { _, selected ->
            if (selected) viewModel.timeGranularityChanged(TimeGranularity.Monthly)
        }
    }

    private fun initObservers() {
        handleErrorsFrom(viewModel)

        viewModel.valueChartData.observe(viewLifecycleOwner, ::loadLineChartData)
        viewModel.placeholderData.observe(viewLifecycleOwner, ::loadPlaceholderLineChartData)

        viewModel.pieChartData.observe(viewLifecycleOwner) { entries ->
            binding.distributionPieChart.setPortions(entries)
        }

        viewModel.shouldShowRefreshIndicator.observe(viewLifecycleOwner) { shouldShow ->
            binding.statsRefreshLayout.isRefreshing = shouldShow
        }

        viewModel.shouldShowEmptyPortionsWarning.observe(viewLifecycleOwner) { shouldShow ->
            binding.pieChartPlaceholder.updateVisibility(shouldShow)
        }

        viewModel.shouldShowPieChart.observe(viewLifecycleOwner) { shouldShow ->
            binding.distributionPieChart.updateVisibility(shouldShow)
        }

        viewModel.shouldShowHistoricPortfolioValuePlaceholder.observe(viewLifecycleOwner) { shouldShow ->
            binding.placeholderLineChart.updateVisibility(shouldShow)
        }

        viewModel.enableTimeGranularitySelection.observe(viewLifecycleOwner) { shouldEnable ->
            binding.dailyValueChip.isEnabled = shouldEnable
            binding.weeklyValueChip.isEnabled = shouldEnable
            binding.monthlyValueChip.isEnabled = shouldEnable
        }

        viewModel.shouldShowHistoricPortfolioValue.observe(viewLifecycleOwner) { shouldShow ->
            val timeGranularity = viewModel.valueChartGranularityStream.value
            if (timeGranularity != null) {
                val chart = chartForGranularity(timeGranularity)
                chart.updateVisibility(shouldShow)
            }
        }

        viewModel.valueChartGranularityStream.observe(viewLifecycleOwner) { granularity ->
            when (granularity) {
                TimeGranularity.Daily -> binding.dailyValueChip.isChecked = true
                TimeGranularity.Weekly -> binding.weeklyValueChip.isChecked = true
                TimeGranularity.Monthly -> binding.monthlyValueChip.isChecked = true
            }
        }
    }

    private fun loadLineChartData(chartData: TimeChartData) {
        val applicableChart = chartForGranularity(chartData.granularity)

        valueCharts.forEach { chart ->
            chart.visibility = if (chart == applicableChart) View.VISIBLE else View.GONE
        }

        val dataSet = LineDataSet(chartData.entries, "value").applyDefaultStyle(chartData.currency)

        applicableChart.apply {
            data = LineData(dataSet)
            xAxis.applyDefaultStyle(chartData)
            setVisibleXRangeMaximum(5f)
            chartData.entries.lastOrNull()?.let { last -> moveViewToX(last.x) }
        }
    }

    private fun loadPlaceholderLineChartData(chartData: TimeChartData) {
        val dataSet = LineDataSet(chartData.entries, "value")
            .applyPlaceholderStyle(chartData.currency)

        binding.placeholderLineChart.apply {
            data = LineData(dataSet)
            xAxis.applyDefaultStyle(chartData)
            setVisibleXRangeMaximum(5f)
            invalidate()
        }
    }

    private fun chartForGranularity(granularity: TimeGranularity): LineChart {
        return when (granularity) {
            TimeGranularity.Daily -> binding.dailyValueLineChart
            TimeGranularity.Weekly -> binding.weeklyValueLineChart
            TimeGranularity.Monthly -> binding.monthlyValueLineChart
        }
    }

    private fun LineChart.applyDefaultStyle() = apply {
        setExtraOffsets(30f, 0f, 30f, 0f)
        setHardwareAccelerationEnabled(true)
        setNoDataText("")
        isHighlightPerTapEnabled = false
        isHighlightPerDragEnabled = false
        description.isEnabled = false
        isAutoScaleMinMaxEnabled = true
        legend.isEnabled = false

        renderer = MentraLineChartRenderer(
            context.getThemeColor(R.attr.lineChartValueBackgroundColor),
            context.getThemeColor(R.attr.lineChartValueColor),
            this,
            animator,
            viewPortHandler
        )

        axisLeft.apply {
            isEnabled = false
            setDrawAxisLine(false)
            setDrawGridLines(false)
            removeAllLimitLines()
            setDrawZeroLine(false)
        }

        axisRight.apply {
            isEnabled = false
        }

        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = requireContext().getThemeColor(R.attr.colorOnSurface)
            setDrawAxisLine(false)
            setDrawGridLines(true)
            enableGridDashedLine(20f, 10f, 0f)
        }

        onChartGestureListener = object : StartEndChartGestureListener() {
            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartGesture?
            ) {
                binding.statsRefreshLayout.isEnabled = false
            }

            override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartGesture?) {
                binding.statsRefreshLayout.isEnabled = true
            }
        }
    }

    private fun LineDataSet.applyDefaultStyle(currency: Currency): LineDataSet = apply {
        val colorPrimary = requireContext().getThemeColor(R.attr.colorPrimary)
        mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        color = colorPrimary
        fillDrawable = getDrawable(requireContext(), R.drawable.line_chart_background)
        valueTextColor = requireContext().getThemeColor(R.attr.colorOnSurface)
        lineWidth = 3f
        circleRadius = 5f
        valueTextSize = 10f
        valueFormatter = ValueAxisFormatter(currency)
        setDrawCircleHole(false)
        setCircleColor(colorPrimary)
        setDrawFilled(true)
    }

    private fun LineChart.applyPlaceholderStyle() = apply {
        applyDefaultStyle()
        onChartGestureListener = null
        isDragEnabled = false

        renderer = MentraLineChartRenderer(
            context.getThemeColor(R.attr.colorDisabled),
            context.getThemeColor(R.attr.colorOnDisabled),
            this,
            animator,
            viewPortHandler
        )
    }

    private fun LineDataSet.applyPlaceholderStyle(currency: Currency): LineDataSet = apply {
        applyDefaultStyle(currency)
        val placeholderColor = requireContext().getThemeColor(R.attr.colorDisabled)
        color = placeholderColor
        setCircleColor(placeholderColor)
        fillDrawable = getDrawable(requireContext(), R.drawable.line_chart_placeholder_background)
    }

    private fun XAxis.applyDefaultStyle(chartData: TimeChartData) {
        valueFormatter = IndexAxisFormatter(chartData.labels)
        axisMinimum = 0f
        axisMaximum = chartData.entries.lastIndex.toFloat()
        isGranularityEnabled = true
        granularity = 1.0f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val valueCharts: List<LineChart>
        get() = listOf(
            binding.dailyValueLineChart,
            binding.weeklyValueLineChart,
            binding.monthlyValueLineChart
        )

}