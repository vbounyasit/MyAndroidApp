package com.example.mykotlinapp.ui.components.view_pager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Helper class for Pager adapters for creating tabs in fragments
 *
 * @property fragment The fragment holding the tabs
 */
abstract class AppPagerAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * The Tab items to create on the fragment
     */
    abstract val tabItems: List<TabItem>

    /**
     * Bundle to pass as arguments to child fragments inflated into each tabs
     */
    open val bundle: Bundle = Bundle()

    /**
     * Map containing badges info for each tabs if they require badges
     */
    private val badges = mutableMapOf<Int, BadgeDrawable>()

    /**
     * Attach the tabs to the fragment
     *
     * @param tabLayout
     * @param pager
     */
    fun attachTabs(tabLayout: TabLayout, pager: ViewPager2) {
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            val tabItem = tabItems[position]
            tabItem.titleResource?.let { tab.text = fragment.resources.getString(it) }
            tabItem.iconResource?.let { tab.setIcon(it) }
            tabItem.badgeNumber?.let {
                val badgeDrawable: BadgeDrawable = tab.orCreateBadge
                badgeDrawable.isVisible = it > 0
                badgeDrawable.number = it
                badges[position] = badgeDrawable
            }
        }.attach()
    }

    /**
     * Sets the number in a given tab's badge
     *
     * @param position The position of the tab to set the number to
     * @param number The number to set the badge to
     */
    fun setBadgeAmount(position: Int, number: Int) {
        badges[position]?.isVisible = number > 0
        badges[position]?.number = number
    }

    override fun getItemCount(): Int = tabItems.size

    override fun createFragment(position: Int): Fragment {
        return tabItems[position].fragment
    }
}
