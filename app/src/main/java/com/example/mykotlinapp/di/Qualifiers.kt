package com.example.mykotlinapp.di

import javax.inject.Qualifier

object Qualifiers {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CircleImageLoader

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class DefaultDispatcher

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class IoDispatcher

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class MainDispatcher

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class MainImmediateDispatcher
}