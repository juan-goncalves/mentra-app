package me.juangoncalves.mentra.features.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import me.juangoncalves.mentra.databinding.OnboardingAutoRefreshFragmentBinding


class OnboardingAutoRefreshFragment : Fragment() {

    private val viewModel: OnboardingViewModel by activityViewModels()

    private var _binding: OnboardingAutoRefreshFragmentBinding? = null
    private val binding get() = _binding!!

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
        binding.previousStepButton.setOnClickListener { viewModel.scrolledToStep(position - 1) }
        // binding.nextStepButton.setOnClickListener { viewModel.scrolledToStep(position + 1) }
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