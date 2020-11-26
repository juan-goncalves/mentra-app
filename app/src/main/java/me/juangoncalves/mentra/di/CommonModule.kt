package me.juangoncalves.mentra.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.juangoncalves.mentra.log.AndroidLogger
import me.juangoncalves.mentra.log.Logger
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class CommonModule {

    @Binds
    @Singleton
    abstract fun bindLogger(impl: AndroidLogger): Logger

    companion object {
        @Provides
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }
    }

}