package me.juangoncalves.mentra.features.onboarding.periodic_refresh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.OnboardingAutoRefreshFragmentBinding
import me.juangoncalves.mentra.extensions.handleErrorsFrom
import me.juangoncalves.mentra.features.onboarding.OnboardingViewModel
import me.juangoncalves.mentra.features.onboarding.SingleChoiceAdapter
import java.time.Duration

@AndroidEntryPoint
class OnboardingAutoRefreshFragment : Fragment(), SingleChoiceAdapter.Listener<Duration> {

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: OnboardingAutoRefreshViewModel by viewModels()

    private var _binding: OnboardingAutoRefreshFragmentBinding? = null
    private val binding get() = _binding!!

    private val durationAdapter = DurationAdapter(this)
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OnboardingAutoRefreshFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView()
        initObservers()
    }

    private fun initObservers() = with(viewModel) {
        handleErrorsFrom(this)

        durationsStream.observe(viewLifecycleOwner) { durations ->
            durationAdapter.data = durations
        }
    }

    private fun configureView() = with(binding) {
        durationRecyclerView.apply {
            adapter = durationAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        previousStepButton.setOnClickListener { onboardingViewModel.scrolledToStep(position - 1) }
        nextStepButton.setOnClickListener { onboardingViewModel.scrolledToStep(position + 1) }
    }

    override fun onOptionSelected(option: Duration) {
        viewModel.periodSelected(option)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int) = OnboardingAutoRefreshFragment().apply {
            arguments = bundleOf("position" to position)
        }
    }

}