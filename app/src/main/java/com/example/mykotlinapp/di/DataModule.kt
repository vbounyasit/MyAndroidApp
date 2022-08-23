package com.example.mykotlinapp.di

import android.content.Context
import com.example.mykotlinapp.model.AppDatabase
import com.example.mykotlinapp.model.dao.*
import com.example.mykotlinapp.model.mappers.impl.user.UserSettingMapper
import com.example.mykotlinapp.network.ApiService
import com.example.mykotlinapp.network.ApiServiceProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao

    @Provides
    fun provideChatDao(appDatabase: AppDatabase): ChatDao = appDatabase.chatDao

    @Provides
    fun provideGroupDao(appDatabase: AppDatabase): GroupDao = appDatabase.groupDao

    @Provides
    fun provideGroupEventDao(appDatabase: AppDatabase): GroupEventDao = appDatabase.groupEventDao

    @Provides
    fun providePostDao(appDatabase: AppDatabase): PostDao = appDatabase.postDao

    @Provides
    fun provideCommentDao(appDatabase: AppDatabase): CommentDao = appDatabase.commentDao

    /**
     * Database
     */

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)

    /**
     * Network
     */

    @Provides
    fun provideApiService(moshi: Moshi): ApiService = ApiServiceProvider.getRetrofitService(moshi)

    /**
     * Preferences
     */

    @Provides
    fun provideSharedPreferencesDao(@ApplicationContext context: Context, @Qualifiers.IoDispatcher dispatcher: CoroutineDispatcher): SharedPreferenceDao = SharedPreferenceDao(context, dispatcher)

    @Provides
    fun provideUserSettingsMapper(@ApplicationContext context: Context): UserSettingMapper = UserSettingMapper(context)

}