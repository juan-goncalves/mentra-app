package me.juangoncalves.mentra.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.data_layer.error.GeneralErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.log.MentraLogger
import me.juangoncalves.mentra.log.AndroidLogger
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class AndroidModule {

    @Binds
    @Singleton
    abstract fun bindLogger(impl: AndroidLogger): MentraLogger

    @Binds
    @Singleton
    abstract fun bindErrorHandler(impl: GeneralErrorHandler): ErrorHandler

}