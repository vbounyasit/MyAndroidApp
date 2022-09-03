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

    data class TimeAgo(val weeks: Int, val days: Int, val hours: Int, val minutes: Int, val default: String)

    /**
     * converts timestamp to a 'time ago' text format for displaying on UI
     *
     * @param context The application context
     * @param time The timestamp to convert
     * @param suffix A suffix to add to the result (ex: '3m ago')
     * @return The resulting time ago text
     */
    fun toFormattedTimeAgo(context: Context, time: Long, suffix: String = ""): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, context.resources.configuration.locales[0])
        val timeAgo = toTimeAgo(time, dateFormat)
        val (weeks, days, hours, minutes, default) = timeAgo
        return if (weeks > 0) default
        else if (days > 0) "${days}d$suffix"
        else if (hours > 0) "${hours}h$suffix"
        else if (minutes > 0) "${minutes}m$suffix"
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
        val dateFormat = DateFormat.getDateTimeInstance(
            DateFormat.NONE,
            DateFormat.SHORT,
            context.resources.configuration.locales[0]
        )
        val timeAgo = toTimeAgo(time, dateFormat)
        val (_, _, hours, minutes, default) = timeAgo
        return if (hours > 0) default
        else if (minutes > 0) "$minutes min"
        else "Now"
    }

    /**
     * Computes the time difference between now and a given time
     *
     * @param time The given time to compute the difference for
     * @param defaultDateFormat The default result format
     * @return A TimeAgo object containing the diff result in weeks, days, hours and minutes
     */
    private fun toTimeAgo(time: Long, defaultDateFormat: DateFormat): TimeAgo {
        val calendar = Calendar.getInstance()
        val date = Date(time)
        fun diff(field: Int) = abs(calendar.fieldDifference(date, field))
        return TimeAgo(
            diff(Calendar.WEEK_OF_YEAR),
            diff(Calendar.DAY_OF_YEAR),
            diff(Calendar.HOUR_OF_DAY),
            diff(Calendar.MINUTE),
            defaultDateFormat.format(date)
        )
    }

    /**
     * Capitalizes a word
     * @receiver a String value
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