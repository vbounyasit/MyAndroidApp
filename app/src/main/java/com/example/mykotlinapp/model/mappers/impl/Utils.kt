package com.example.mykotlinapp.model.mappers.impl

import android.content.Context
import android.icu.text.DateFormat
import android.icu.text.DecimalFormat
import android.icu.util.Calendar
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.ActivityStatus
import com.example.mykotlinapp.domain.pojo.VoteState
import java.math.RoundingMode
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

object Utils {

    /**
     * converts timestamp to a 'time ago' text format for displaying on UI
     *
     * @param context The application context
     * @param time The timestamp to convert
     * @param suffix A suffix to add to the result (ex: '3m ago')
     * @return The resulting time ago text
     */
    fun toTimeAgo(context: Context, time: Long, suffix: String = ""): String {
        val calendar = Calendar.getInstance()
        val lastLogDate = Date(time)
        val default = DateFormat.getDateInstance(
            DateFormat.MEDIUM,
            context.resources.configuration.locales[0]
        ).format(Date(time))

        fun diff(field: Int) = abs(calendar.fieldDifference(lastLogDate, field))
        val weeksAgo = diff(Calendar.WEEK_OF_YEAR)
        val daysAgo = diff(Calendar.DAY_OF_YEAR)
        val hoursAgo = diff(Calendar.HOUR_OF_DAY)
        val minutesAgo = diff(Calendar.MINUTE)
        return if (weeksAgo > 0) default
        else if (daysAgo > 0) "${daysAgo}d$suffix"
        else if (hoursAgo > 0) "${hoursAgo}h$suffix"
        else if (minutesAgo > 1) "${minutesAgo}m$suffix"
        else "Now"
    }

    /**
     * Converts a timestamp to a 'time ago' text format for chat logs
     *
     * @param context The application context
     * @param time The timestamp to convert
     * @return The resulting text
     */
    fun toChatLogTime(context: Context, time: Long): String {
        val calendar = Calendar.getInstance()
        val lastLogDate = Date(time)
        fun diff(field: Int) = abs(calendar.fieldDifference(lastLogDate, field))
        val hoursAgo = diff(Calendar.HOUR_OF_DAY)
        val minutesAgo = diff(Calendar.MINUTE)
        return if (hoursAgo > 0) DateFormat.getDateTimeInstance(
            DateFormat.NONE,
            DateFormat.SHORT,
            context.resources.configuration.locales[0]
        ).format(time).toString()
        else if (minutesAgo > 0) "$minutesAgo min"
        else "Now"
    }

    /**
     * Capitalizes a word
     *
     * @param word The word to capitalize
     */
    fun String.toCapitalized() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    fun truncate(number: Double): String {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.HALF_DOWN.ordinal
        return df.format(number)
    }

    /**
     * Formats big numbers to readable truncated amounts (ex: 1 200 000 to 1.2M, 1 500 to 1.5k ...etc)
     *
     * @param truncate function that truncates a double and return the resulting text
     * @receiver any Int number
     * @return The resulting formatted number
     */
    inline fun Int.getFormattedAmount(truncate: (Double) -> String = ::truncate): String {
        return if (this > 10.0.pow(6))
            "${truncate(this / 10.0.pow(6))}M"
        else if (this > 10.0.pow(3))
            "${truncate(this / 10.0.pow(3))}K"
        else "$this"
    }

    /**
     * Get activity status based on Idle time
     *
     * @param context The application context
     * @param now The current timestamp
     * @param lastActive The last active timestamp
     * @return The correct ActivityStatus
     */
    fun toActivityStatus(context: Context, now: Long, lastActive: Long): ActivityStatus {
        val inactivityTime = (now - lastActive) / 1000 / 60
        val maxInactivity = context.resources.getInteger(R.integer.minutes_to_inactive)
        return if (inactivityTime < maxInactivity) ActivityStatus.ONLINE else ActivityStatus.AWAY
    }

    fun Int.getVoteStateValue(): VoteState = VoteState.values().first { it.value == this }
}