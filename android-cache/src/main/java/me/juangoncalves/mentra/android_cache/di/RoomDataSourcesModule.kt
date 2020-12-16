package me.juangoncalves.mentra.android_cache.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.android_cache.sources.RoomCoinDataSource
import me.juangoncalves.mentra.android_cache.sources.RoomCurrencyDataSource
import me.juangoncalves.mentra.android_cache.sources.RoomPortfolioDataSource
import me.juangoncalves.mentra.android_cache.sources.RoomWalletDataSource
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.portfolio.PortfolioLocalDataSource
import me.juangoncalves.mentra.data_layer.sources.wallet.WalletLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class RoomDataSourcesModule {

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

}