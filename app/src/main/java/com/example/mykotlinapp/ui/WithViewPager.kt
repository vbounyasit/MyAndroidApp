package com.example.mykotlinapp.ui

import com.example.mykotlinapp.ui.components.view_pager.AppPagerAdapter

interface WithViewPager {
    fun getPagerAdapter(): AppPagerAdapter
}