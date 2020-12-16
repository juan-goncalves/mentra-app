package me.juangoncalves.mentra.android_network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.android_network.sources.CryptoIconsDataSource
import me.juangoncalves.mentra.android_network.sources.RetrofitCoinDataSource
import me.juangoncalves.mentra.android_network.sources.RetrofitCurrencyDataSource
import me.juangoncalves.mentra.data_layer.sources.coin.CoinIconDataSource
import me.juangoncalves.mentra.data_layer.sources.coin.CoinRemoteDataSource
import me.juangoncalves.mentra.data_layer.sources.currency.CurrencyRemoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class RetrofitDataSourcesModule {

    @Binds
    @Singleton
    abstract fun bindCoinRemoteDataSource(impl: RetrofitCoinDataSource): CoinRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCoinIconDataSource(impl: CryptoIconsDataSource): CoinIconDataSource

    @Binds
    @Singleton
    abstract fun bindCurrencyRemoteDataSource(impl: RetrofitCurrencyDataSource): CurrencyRemoteDataSource

}