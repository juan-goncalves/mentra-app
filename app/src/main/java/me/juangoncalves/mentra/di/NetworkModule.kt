package me.juangoncalves.mentra.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.core.network.CryptoCompareService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideCryptoCompareService(httpClient: OkHttpClient): CryptoCompareService {
        return Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/") // TODO: Make build config variable for different flavors
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
            .create(CryptoCompareService::class.java)
    }

}