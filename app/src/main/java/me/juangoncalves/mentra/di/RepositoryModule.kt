package me.juangoncalves.mentra.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.data.repositories.CoinRepositoryImpl
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

}