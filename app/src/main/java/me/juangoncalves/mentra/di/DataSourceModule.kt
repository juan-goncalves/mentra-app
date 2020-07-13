package me.juangoncalves.mentra.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.data.sources.CoinLocalDataSource
import me.juangoncalves.mentra.data.sources.CoinLocalDataSourceImpl
import me.juangoncalves.mentra.data.sources.CoinRemoteDataSource
import me.juangoncalves.mentra.data.sources.CoinRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindCoinRemoteDataSource(impl: CoinRemoteDataSourceImpl): CoinRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCoinLocalDataSource(impl: CoinLocalDataSourceImpl): CoinLocalDataSource

}