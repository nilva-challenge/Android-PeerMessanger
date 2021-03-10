package com.peer_messanger.di.module

import android.content.Context
import androidx.room.Room
import com.peer_messanger.data.dao.DeviceDao
import com.peer_messanger.data.dao.MessageDao
import com.peer_messanger.data.local.database.AppDatabase
import com.peer_messanger.data.repository.RealRepository
import com.peer_messanger.data.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "db").build()


    @Singleton
    @Provides
    fun provideDeviceDao(db: AppDatabase) = db.deviceDao()

    @Singleton
    @Provides
    fun provideMessageDao(db: AppDatabase) = db.messageDao()

    @Singleton
    @Provides
    fun provideRepository(
        deviceDao: DeviceDao,
        messageDao: MessageDao
    ): Repository = RealRepository(
        deviceDao,
        messageDao
    )
}