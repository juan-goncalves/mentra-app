package me.juangoncalves.mentra.android_network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.juangoncalves.mentra.android_network.services.crypto_compare.CryptoCompareApi
import me.juangoncalves.mentra.android_network.services.crypto_icons.CryptoIconsService
import me.juangoncalves.mentra.android_network.services.currency_layer.CurrencyLayerApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
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
    fun provideCryptoCompareService(httpClient: OkHttpClient): CryptoCompareApi {
        return Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/") // TODO: Make build config variable for different flavors
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
            .create(CryptoCompareApi::class.java)
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
    fun provideCurrencyLayerApi(httpClient: OkHttpClient): CurrencyLayerApi {
        return Retrofit.Builder()
            .baseUrl("http://api.currencylayer.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
            .create(CurrencyLayerApi::class.java)
    }

}