package me.juangoncalves.mentra.ui.wallet_list

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.juangoncalves.mentra.databinding.DeleteWalletConfirmationDialogFragmentBinding

class DeleteWalletConfirmationDialogFragment(
    private val onCancel: () -> Unit,
    private val onConfirm: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DeleteWalletConfirmationDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private var confirmed: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DeleteWalletConfirmationDialogFragmentBinding.inflate(inflater, container, false)
        isCancelable = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.deleteButton.setOnClickListener {
            confirmed = true
            dismiss()
        }
        binding.cancelButton.setOnClickListener {
            confirmed = false
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (confirmed) onConfirm() else onCancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
