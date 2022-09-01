package com.example.mykotlinapp.di

import android.app.NotificationManager
import android.content.Context
import androidx.work.WorkManager
import com.example.mykotlinapp.di.Qualifiers.IoDispatcher
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.network.socket.SocketComponent
import com.example.mykotlinapp.ui.components.DialogFormFragmentManager
import com.example.mykotlinapp.ui.components.drawer.BottomDrawerManager
import com.example.mykotlinapp.ui.components.notifications.NotificationComponent
import com.example.mykotlinapp.utils.AppTimeProvider
import com.example.mykotlinapp.utils.TimeProvider
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
class ComponentsModule {

    @Provides
    fun getNavigationDrawerComponent(): BottomDrawerManager = BottomDrawerManager()

    @Provides
    fun getDialogFormManager(): DialogFormFragmentManager = DialogFormFragmentManager()

    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    fun provideSocketComponent(
        sharedPreferenceDao: SharedPreferenceDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        moshi: Moshi
    ): SocketComponent =
        SocketComponent(sharedPreferenceDao, dispatcher, moshi)

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService(NotificationManager::class.java)

    @Provides
    fun provideNotificationComponent(
        notificationManager: NotificationManager,
        @ApplicationContext context: Context,
        @Qualifiers.DefaultDispatcher dispatcher: CoroutineDispatcher
    ) =
        NotificationComponent(notificationManager, context, dispatcher)

    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    fun provideTimeUtils(): TimeProvider = AppTimeProvider()
}