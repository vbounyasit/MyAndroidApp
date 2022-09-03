package com.example.mykotlinapp.utils

interface TimeProvider {
    fun provideCurrentTimeMillis(): Long

}