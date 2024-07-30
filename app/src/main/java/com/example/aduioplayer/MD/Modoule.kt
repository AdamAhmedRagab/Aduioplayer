package com.example.aduioplayer.MD

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.aduioplayer.db.AppDataBase
import com.example.aduioplayer.db.Converters
import com.example.aduioplayer.db.PlayListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Modoule {
    @Singleton
    @Provides
    fun providesDao(appDataBase: AppDataBase): PlayListDao = appDataBase.dao()

    @Provides
    @Singleton
    fun providesAppDb(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, "AppDB")
            .setQueryCallback({ sqlQuery, bindArgs ->
                Log.d("SQL Query", "Query: $sqlQuery SQL Args: $bindArgs")
            }, Executors.newSingleThreadExecutor())
            .fallbackToDestructiveMigration().build()
}