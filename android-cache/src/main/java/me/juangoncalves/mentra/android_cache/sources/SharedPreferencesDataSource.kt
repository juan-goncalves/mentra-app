package me.juangoncalves.mentra.android_cache.sources

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import me.juangoncalves.mentra.data_layer.sources.preferences.PreferenceLocalDataSource
import javax.inject.Inject

class SharedPreferencesDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PreferenceLocalDataSource, SharedPreferences.OnSharedPreferenceChangeListener {

    private val _liveUpdates: MutableMap<String, MutableSharedFlow<String?>> = hashMapOf()

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override suspend fun putString(key: String, value: String?) =
        sharedPreferences.edit(commit = true) { putString(key, value) }


    override suspend fun getString(key: String): String? = sharedPreferences.getString(key, null)

    override fun liveUpdatesFor(key: String): Flow<String?> {
        return _liveUpdates.getOrPut(key) {
            val flow = MutableSharedFlow<String?>(
                replay = 1,
                onBufferOverflow = BufferOverflow.DROP_LATEST
            )
            val current = sharedPreferences.getString(key, null)
            flow.tryEmit(current)
            flow
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        if (key == null) return

        val value = prefs?.getString(key, null)

        _liveUpdates[key]?.run {
            tryEmit(value)
        }
    }

}