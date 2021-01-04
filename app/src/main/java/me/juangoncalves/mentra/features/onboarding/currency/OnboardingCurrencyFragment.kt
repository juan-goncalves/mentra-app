package me.juangoncalves.mentra.features.onboarding.currency

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
import me.juangoncalves.mentra.databinding.OnboardingCurrencyFragmentBinding
import me.juangoncalves.mentra.extensions.updateVisibility
import me.juangoncalves.mentra.features.onboarding.OnboardingViewModel
import me.juangoncalves.mentra.features.onboarding.currency.OnboardingCurrencyViewModel.Error

@AndroidEntryPoint
class OnboardingCurrencyFragment : Fragment() {

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: OnboardingCurrencyViewModel by viewModels()

    private var _binding: OnboardingCurrencyFragmentBinding? = null
    private val binding get() = _binding!!

    private var position: Int = 0
    private val currencyAdapter = CurrencyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OnboardingCurrencyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView()
        initObservers()
    }

    private fun configureView() = with(binding) {
        currencyRecyclerView.apply {
            adapter = currencyAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        retryButton.setOnClickListener { viewModel.retrySelected() }
        previousStepButton.setOnClickListener { onboardingViewModel.scrolledToStep(position - 1) }
    }

    private fun initObservers() = with(viewModel) {
        currenciesStream.observe(viewLifecycleOwner) { currencies ->
            currencyAdapter.data = currencies
        }

        errorStateStream.observe(viewLifecycleOwner) { error ->
            when (error) {
                Error.None -> binding.errorStateView.updateVisibility(false)
                Error.CurrenciesNotLoaded -> binding.errorStateView.updateVisibility(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int) = OnboardingCurrencyFragment().apply {
            arguments = bundleOf("position" to position)
        }
    }

}