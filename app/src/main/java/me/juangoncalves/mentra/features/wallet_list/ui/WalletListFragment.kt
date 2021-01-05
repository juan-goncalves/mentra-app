package me.juangoncalves.mentra.features.wallet_list.ui

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.juangoncalves.mentra.common.BundleKeys
import me.juangoncalves.mentra.common.RequestKeys
import me.juangoncalves.mentra.databinding.WalletListFragmentBinding
import me.juangoncalves.mentra.extensions.*
import me.juangoncalves.mentra.features.wallet_creation.ui.WalletCreationActivity
import me.juangoncalves.mentra.features.wallet_deletion.DeleteWalletDialogFragment
import me.juangoncalves.mentra.features.wallet_edit.EditWalletDialogFragment
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewModel
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WalletListFragment : Fragment(), WalletSwipeHelper.Listener {

    private val viewModel: WalletListViewModel by viewModels()
    private val walletAdapter: WalletAdapter = WalletAdapter()

    private var _binding: WalletListFragmentBinding? = null
    private val binding get() = _binding!!

    private var observersInitialized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initialize()

        setFragmentResultListener(RequestKeys.WalletDeletion) { _, bundle ->
            processWalletActionResult(bundle, BundleKeys.WalletDeletionResult)
        }

        setFragmentResultListener(RequestKeys.WalletEdit) { _, bundle ->
            processWalletActionResult(bundle, BundleKeys.WalletEditResult)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WalletListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Initialize the observers after the enter transition ended to prevent stuttering */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (nextAnim == 0) return null

        val animator = AnimatorInflater.loadAnimator(activity, nextAnim)

        val listener = object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                initObservers()
                animator.removeListener(this)
            }

            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationRepeat(p0: Animator?) {}
        }

        animator.addListener(listener)
        return animator
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView(view)
        initObservers()
    }

    private fun configureView(view: View) {
        ViewCompat.requestApplyInsets(view)

        binding.refreshLayout.styleByTheme().setOnRefreshListener {
            viewModel.refreshSelected()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = walletAdapter
            val touchHelper = WalletTouchHelper(
                requireContext(),
                this@WalletListFragment,
                binding.refreshLayout
            )
            touchHelper.attachToRecyclerView(this)
        }

        binding.addWalletButton.setOnClickListener {
            val intent = Intent(requireContext(), WalletCreationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initObservers() {
        if (observersInitialized) return
        observersInitialized = true

        handleErrorsFrom(viewModel, binding.addWalletButton)

        viewModel.viewStateStream.observe(viewLifecycleOwner) { state ->
            walletAdapter.data = state.wallets
            binding.walletsLoadingIndicator.animateVisibility(state.isLoadingWallets, 300L)
            binding.refreshLayout.isRefreshing = state.isRefreshingPrices
            binding.emptyStateView.animateVisibility(state.isEmpty, 300L)
            bindErrorState(state)
        }
    }

    private fun bindErrorState(state: WalletListViewState) {
        when (state.error) {
            WalletListViewState.Error.None -> {
                binding.refreshLayout.isEnabled = true
                binding.recyclerView.animateVisibility(true, 300L)
                binding.loadWalletsErrorStateView.hide()
            }
            WalletListViewState.Error.WalletsNotLoaded -> {
                binding.refreshLayout.isEnabled = false
                binding.recyclerView.animateVisibility(false, 300L)
                binding.loadWalletsErrorStateView.show()
            }
        }
    }

    override fun onDeleteWalletGesture(position: Int) {
        DeleteWalletDialogFragment
            .newInstance(walletAdapter.data[position])
            .show(parentFragmentManager, "delete_wallet")
    }

    override fun onEditWalletGesture(position: Int) {
        EditWalletDialogFragment
            .newInstance(walletAdapter.data[position])
            .show(parentFragmentManager, "edit_wallet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Resets the recycler view item swipe position if the action was cancelled.
     *
     * For example: when the user swipes to the left to delete a wallet and then
     * he dismisses the dialog, we have to reset the item back to its original
     * state (e.g hide the delete bubble).
     */
    private fun processWalletActionResult(bundle: Bundle, resultKey: String) {
        val wallet = bundle.getParcelable<WalletListViewState.Wallet>(BundleKeys.Wallet)
        val wasModified = bundle.getBoolean(resultKey, false)
        if (wasModified) return

        val position = walletAdapter.data.indexOfFirst { it == wallet }
        if (position > -1) walletAdapter.notifyItemChanged(position)
    }

}
