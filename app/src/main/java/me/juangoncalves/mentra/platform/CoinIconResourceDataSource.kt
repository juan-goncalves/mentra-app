package me.juangoncalves.mentra.platform

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import me.juangoncalves.mentra.data_layer.sources.coin.CoinIconDataSource
import me.juangoncalves.mentra.domain_layer.models.Coin
import javax.inject.Inject

/** Provides URIs to coin icons that are stored locally as raw resources. */
class CoinIconResourceDataSource @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : CoinIconDataSource {

    override suspend fun getAlternativeIconFor(coin: Coin): String? {
        val resourceId = appContext.resources.getIdentifier(
            coin.symbol.lowercase(),
            "raw",
            appContext.packageName
        )

        if (resourceId == 0) {
            return null
        }

        val uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(appContext.packageName)
            .appendPath(appContext.resources.getResourceTypeName(resourceId))
            .appendPath(resourceId.toString())
            .build()

        return uri.toString()
    }
}