package me.juangoncalves.mentra.features.onboarding.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.OnboardingFinishedFragmentBinding
import me.juangoncalves.mentra.extensions.empty
import me.juangoncalves.mentra.features.dashboard.DashboardActivity
import me.juangoncalves.mentra.features.onboarding.OnboardingViewModel

@AndroidEntryPoint
class OnboardingFinishedFragment : Fragment() {

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    private var _binding: OnboardingFinishedFragmentBinding? = null
    private val binding get() = _binding!!

    private var position: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OnboardingFinishedFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView()
        initObservers()
    }

    private fun initObservers() = empty()

    private fun configureView() = with(binding) {
        finishButton.setOnClickListener {
            val intent = Intent(requireContext(), DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int) = OnboardingFinishedFragment().apply {
            arguments = bundleOf("position" to position)
        }
    }

}