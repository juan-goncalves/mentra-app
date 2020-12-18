package me.juangoncalves.mentra.data_layer.sources.preferences

import kotlinx.coroutines.flow.Flow

interface PreferenceLocalDataSource {

    fun liveUpdatesFor(key: String): Flow<String?>

    suspend fun putString(key: String, value: String?)

    suspend fun getString(key: String): String?

}