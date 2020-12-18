package me.juangoncalves.mentra.di

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.juangoncalves.mentra.android_cache.error.RoomErrorHandler
import me.juangoncalves.mentra.android_cache.sources.*
import me.juangoncalves.mentra.data_layer.di.LocalErrorHandler
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.portfolio.PortfolioLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.preferences.PreferenceLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class CacheModule {

    companion object {
        @Provides
        @Singleton
        fun providePreferenceDataSource(@ApplicationContext context: Context): PreferenceLocalDataSource {
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesDataSource(sharedPrefs)
        }
    }

    @Binds
    @Singleton
    abstract fun bindCoinLocalDataSource(impl: RoomCoinDataSource): CoinLocalDataSource

    @Binds
    @Singleton
    abstract fun bindWalletLocalDataSource(impl: RoomWalletDataSource): WalletLocalDataSource

    @Binds
    @Singleton
    abstract fun bindCurrencyLocalDataSource(impl: RoomCurrencyDataSource): CurrencyLocalDataSource

    @Binds
    @Singleton
    abstract fun bindPortfolioLocalDataSource(impl: RoomPortfolioDataSource): PortfolioLocalDataSource

    @LocalErrorHandler
    @Binds
    @Singleton
    abstract fun bindLocalErrorHandler(impl: RoomErrorHandler): ErrorHandler

}