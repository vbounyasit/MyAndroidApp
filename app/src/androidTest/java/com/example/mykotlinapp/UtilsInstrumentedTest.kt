package com.example.mykotlinapp

import android.icu.util.Calendar
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mykotlinapp.model.mappers.impl.Utils
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsInstrumentedTest {

    @Test
    fun test() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val calendar = Calendar.getInstance()
        //Given
        val timeAgo = 1659559220L
        //When
        val timeAgoText = Utils.toTimeAgo(appContext, timeAgo)
        //Then
        assert(timeAgoText == "Gey")
    }
}