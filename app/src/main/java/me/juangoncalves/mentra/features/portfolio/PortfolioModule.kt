package me.juangoncalves.mentra.features.portfolio

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.features.portfolio.data.repositories.CoinRepositoryImpl
import me.juangoncalves.mentra.features.portfolio.data.sources.CoinLocalDataSource
import me.juangoncalves.mentra.features.portfolio.data.sources.CoinLocalDataSourceImpl
import me.juangoncalves.mentra.features.portfolio.data.sources.CoinRemoteDataSource
import me.juangoncalves.mentra.features.portfolio.data.sources.CoinRemoteDataSourceImpl
import me.juangoncalves.mentra.features.portfolio.domain.repositories.CoinRepository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class PortfolioModule {

    @Binds
    @Singleton
    abstract fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

    @Binds
    @Singleton
    abstract fun bindCoinRemoteDataSource(impl: CoinRemoteDataSourceImpl): CoinRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCoinLocalDataSource(impl: CoinLocalDataSourceImpl): CoinLocalDataSource

}