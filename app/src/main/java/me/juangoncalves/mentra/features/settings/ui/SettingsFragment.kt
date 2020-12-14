package me.juangoncalves.mentra.features.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.BuildConfig
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.extensions.applyErrorStyle
import me.juangoncalves.mentra.extensions.toDp
import me.juangoncalves.mentra.features.settings.model.SettingsViewModel


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by activityViewModels()

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView()
        initObservers()
    }

    private fun configureView() {
        setDivider(ContextCompat.getDrawable(requireContext(), R.drawable.preference_divider))
        setDividerHeight(1.toDp(requireContext()))
        listView.setPadding(14.toDp(requireContext()), 0, 14.toDp(requireContext()), 0)
        listView.clipToPadding = false
    }

    private fun initObservers() {
        viewModel.showErrorSnackbarStream.observe(viewLifecycleOwner) { event ->
            event.use { messageId ->
                Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT)
                    .applyErrorStyle()
                    .show()
            }
        }

        viewModel.showSuccessSnackbarStream.observe(viewLifecycleOwner) { event ->
            event.use { messageId ->
                Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.availableCurrenciesStream.observe(viewLifecycleOwner) { currencies ->
            currencyCodePref.apply {
                entries = currencies.map { "${it.currencyCode} (${it.symbol})" }.toTypedArray()
                entryValues = currencies.map { it.currencyCode }.toTypedArray()
                isEnabled = true
            }
        }

        viewModel.durationsStream.observe(viewLifecycleOwner) { options ->
            periodicRefreshPref.apply {
                entries = options.map { getString(it.labelId) }.toTypedArray()
                entryValues = options.map { it.value }.toTypedArray()
            }
        }

        viewModel.showLoadingIndicatorStream.observe(viewLifecycleOwner) { shouldShow ->
            refreshCoinsPref.isEnabled = !shouldShow
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        versionPref.summary = BuildConfig.VERSION_NAME
        buildTypePref.summary = BuildConfig.BUILD_TYPE.capitalize()

        refreshCoinsPref.setOnPreferenceClickListener {
            viewModel.refreshCoinsSelected()
            true
        }

        licensesPref.setOnPreferenceClickListener {
            val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
            startActivity(intent)
            true
        }
    }

    private val versionPref: Preference get() = findPreference("app_version")!!
    private val currencyCodePref: ListPreference get() = findPreference("currency_code")!!
    private val periodicRefreshPref: ListPreference get() = findPreference("periodic_refresh")!!
    private val buildTypePref: Preference get() = findPreference("build_type")!!
    private val refreshCoinsPref: Preference get() = findPreference("refresh_coins")!!
    private val licensesPref: Preference get() = findPreference("licenses")!!

}