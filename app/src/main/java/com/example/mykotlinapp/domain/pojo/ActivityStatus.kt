package com.example.mykotlinapp.domain.pojo

import com.example.mykotlinapp.R

/**
 * User activity status
 *
 * @property icon The icon to show for a given activity
 * @constructor Create Activity status with a given icon
 */
enum class ActivityStatus(val icon: Int) {
    ONLINE(R.drawable.ic_status_online),
    BUSY(R.drawable.ic_status_busy),
    AWAY(R.drawable.ic_status_away)
}