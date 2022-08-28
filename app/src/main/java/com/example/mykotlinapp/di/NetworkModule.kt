package com.example.mykotlinapp.di

import com.example.mykotlinapp.network.ApiServiceProvider
import com.example.mykotlinapp.network.service.*
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    fun provideChatApiService(moshi: Moshi): ChatApiService =
        ApiServiceProvider.getRetrofitService(moshi)

    @Provides
    fun provideGroupApiService(moshi: Moshi): GroupApiService =
        ApiServiceProvider.getRetrofitService(moshi)

    @Provides
    fun providePostApiService(moshi: Moshi): PostApiService =
        ApiServiceProvider.getRetrofitService(moshi)

    @Provides
    fun provideCommentApiService(moshi: Moshi): CommentApiService =
        ApiServiceProvider.getRetrofitService(moshi)

    @Provides
    fun provideUserApiService(moshi: Moshi): UserApiService =
        ApiServiceProvider.getRetrofitService(moshi)
}