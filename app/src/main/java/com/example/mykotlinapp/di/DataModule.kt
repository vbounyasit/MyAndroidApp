package com.example.mykotlinapp.di

import android.content.Context
import com.example.mykotlinapp.model.AppDatabase
import com.example.mykotlinapp.model.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    /**
     * Database
     */

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    /**
     * DAO
     */

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
     * Preferences
     */

    @Provides
    fun provideSharedPreferencesDao(
        @ApplicationContext context: Context,
        @Qualifiers.IoDispatcher dispatcher: CoroutineDispatcher
    ): SharedPreferenceDao = SharedPreferenceDao(context, dispatcher)

}