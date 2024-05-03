package com.kan.dev.familyhealth.di

import android.content.Context
import com.kan.dev.familyhealth.data.DatabaseApp
import com.kan.dev.familyhealth.data.dao.BMIDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): DatabaseApp {
        return DatabaseApp.getDatabase(context)
    }
    @Singleton
    @Provides
    fun provideBMIDao(databaseApp: DatabaseApp): BMIDao {
        return databaseApp.bmiDAO()
    }
}