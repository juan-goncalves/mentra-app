package me.juangoncalves.mentra.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.juangoncalves.mentra.network.CryptoCompareService
import me.juangoncalves.mentra.network.CryptoIconsService
import me.juangoncalves.mentra.network.ExchangeRateService
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

    @Provides
    @Singleton
    fun provideCryptoIconsService(httpClient: OkHttpClient): CryptoIconsService {
        return Retrofit.Builder()
            .baseUrl("https://cryptoicons.org/") // TODO: Make build config variable for different flavors
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
            .create(CryptoIconsService::class.java)
    }

    @Provides
    @Singleton
    fun provideExchangeRateService(httpClient: OkHttpClient): ExchangeRateService {
        return Retrofit.Builder()
            .baseUrl("https://api.exchangeratesapi.io/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
            .create(ExchangeRateService::class.java)
    }

}