package me.juangoncalves.mentra.data_layer.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class NetworkErrorHandler

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class LocalErrorHandler