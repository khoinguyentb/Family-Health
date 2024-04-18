package com.kan.dev.familyhealth.di

import android.content.Context
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import com.kan.dev.familyhealth.utils.SystemUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideSharePreferencesUtils(@ApplicationContext context: Context): SharePreferencesUtils {
        return SharePreferencesUtils(context)
    }

    @Provides
    @Singleton
    fun provideSystemUtils(@ApplicationContext context: Context) : SystemUtils {
        return SystemUtils(context)
    }
}