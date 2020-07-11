package me.juangoncalves.mentra.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.core.log.AndroidLogger
import me.juangoncalves.mentra.core.log.Logger
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class CommonModule {

    @Binds
    @Singleton
    abstract fun bindLogger(impl: AndroidLogger): Logger

}