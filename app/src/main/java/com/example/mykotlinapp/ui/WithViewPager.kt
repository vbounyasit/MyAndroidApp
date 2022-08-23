package com.example.mykotlinapp.ui

import com.example.mykotlinapp.ui.components.view_pager.AppPagerAdapter

/**
 * Interface implemented by fragments/activities to setup a viewPager with tabs
 */
interface WithViewPager {
    fun getPagerAdapter(): AppPagerAdapter
}