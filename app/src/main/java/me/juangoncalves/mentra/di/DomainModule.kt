package me.juangoncalves.mentra.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.data.repositories.*
import me.juangoncalves.mentra.domain_layer.repositories.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class DomainModule {

    @Binds
    @Singleton
    abstract fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(impl: WalletRepositoryImpl): WalletRepository

    @Binds
    @Singleton
    abstract fun bindPortfolioRepository(impl: PortfolioRepositoryImpl): PortfolioRepository

    @Binds
    @Singleton
    abstract fun bindIconRepository(impl: IconRepositoryImpl): IconRepository

    @Binds
    @Singleton
    abstract fun bindPreferenceRepository(impl: SharedPreferencesRepository): PreferenceRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository

}