package com.example.mykotlinapp.ui.components.view_pager

import androidx.fragment.app.Fragment

/**
 * Data class for a given tab in a ViewPager
 *
 * @property titleResource The string resource for the tab label
 * @property iconResource The drawable resource for the tab icon
 * @property fragment The fragment to bind to the tab
 * @property badgeNumber The optional initial badge number for the tab
 */
data class TabItem(
    val titleResource: Int? = null,
    val iconResource: Int? = null,
    val fragment: Fragment,
    val badgeNumber: Int? = null,
)