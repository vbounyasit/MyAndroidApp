package com.example.mykotlinapp.utils

class AppTimeProvider : TimeProvider {
    override fun provideCurrentTimeMillis(): Long = System.currentTimeMillis()
}