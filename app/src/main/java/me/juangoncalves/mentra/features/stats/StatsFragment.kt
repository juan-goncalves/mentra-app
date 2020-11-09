package me.juangoncalves.mentra.features.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.math.MathUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.StatsFragmentBinding
import me.juangoncalves.mentra.extensions.getThemeColor
import me.juangoncalves.mentra.extensions.showSnackbarOnFleetingErrors
import me.juangoncalves.mentra.extensions.styleByTheme


@AndroidEntryPoint
class StatsFragment : Fragment() {

    private val viewModel: StatsViewModel by viewModels()

    private var _binding: StatsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        styleLineChart()

        binding.statsRefreshLayout.styleByTheme().setOnRefreshListener {
            viewModel.refreshSelected()
        }

        initObservers()
    }

    private fun initObservers() {
        showSnackbarOnFleetingErrors(viewModel)

        viewModel.valueChartData.observe(viewLifecycleOwner) { (entries, indicesToDates) ->
            val dateAxisFormatter = DateAxisFormatter(indicesToDates)
            val dataSet = LineDataSet(entries, "value").styled()
            val lineData = LineData(dataSet)

            binding.valueLineChart.apply {
                data = lineData
                xAxis.valueFormatter = dateAxisFormatter
                xAxis.setLabelCount(MathUtils.clamp(entries.size, 0, 5), true)
                setVisibleXRangeMaximum(5f)
                moveViewToX(entries.lastIndex.toFloat())
                notifyDataSetChanged()
            }
        }

        viewModel.pieChartData.observe(viewLifecycleOwner) { entries ->
            binding.distributionPieChart.setPortions(entries)
        }

        viewModel.shouldShowRefreshIndicator.observe(viewLifecycleOwner) { shouldShow ->
            binding.statsRefreshLayout.isRefreshing = shouldShow
        }
    }

    private fun styleLineChart() = with(binding.valueLineChart) {
        setExtraOffsets(30f, 0f, 30f, 0f)
        animateXY(250, 250)

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
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }
    }

    private fun LineDataSet.styled(): LineDataSet = apply {
        val colorPrimary = requireContext().getThemeColor(R.attr.colorPrimary)
        mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        color = colorPrimary
        fillDrawable = getDrawable(requireContext(), R.drawable.line_chart_background)
        valueTextColor = requireContext().getThemeColor(R.attr.colorOnSurface)
        lineWidth = 3f
        circleRadius = 5f
        valueTextSize = 10f
        valueFormatter = ValueAxisFormatter()
        setDrawCircleHole(false)
        setCircleColor(colorPrimary)
        setDrawFilled(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}