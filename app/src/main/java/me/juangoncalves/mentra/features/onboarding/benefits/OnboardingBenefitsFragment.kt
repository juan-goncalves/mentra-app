package me.juangoncalves.mentra.features.onboarding.benefits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import me.juangoncalves.mentra.databinding.OnboardingBenefitsFragmentBinding
import me.juangoncalves.mentra.features.onboarding.OnboardingViewModel


class OnboardingBenefitsFragment : Fragment() {

    private val viewModel: OnboardingViewModel by activityViewModels()

    private var _binding: OnboardingBenefitsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OnboardingBenefitsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startButton.setOnClickListener { viewModel.startSelected() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = OnboardingBenefitsFragment()
    }

}