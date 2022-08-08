package com.example.mykotlinapp.domain.pojo

import com.example.mykotlinapp.R

/**
 * Event join state
 *
 * @property label The label to show for the given state
 * @property icon The icon to show for the given state
 * @constructor Create an Event join state
 */
enum class EventJoinState(val label: String, val icon: Int) {
    JOINED("Going", R.drawable.ic_event_joined),
    MAYBE("Maybe", R.drawable.ic_event_maybe),
    DECLINED("Not going", R.drawable.ic_event_declined)
}