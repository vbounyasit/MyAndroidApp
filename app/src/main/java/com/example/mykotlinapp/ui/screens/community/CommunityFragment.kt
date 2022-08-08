package com.example.mykotlinapp.ui.screens.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.FragmentMainCommunityBinding
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.WithViewPager
import com.example.mykotlinapp.ui.components.view_pager.AppPagerAdapter
import com.example.mykotlinapp.ui.components.view_pager.TabItem
import com.example.mykotlinapp.ui.screens.community.channels.ChannelFragment
import com.example.mykotlinapp.ui.screens.community.feed.FeedFragment

class CommunityFragment : AppFragment(), WithViewPager {

    private lateinit var binding: FragmentMainCommunityBinding

    override fun getPagerAdapter(): AppPagerAdapter {
        return object : AppPagerAdapter(this) {
            override val tabItems: List<TabItem>
                get() = listOf(
                    TabItem(R.string.feed_tab_label, R.drawable.ic_feed, FeedFragment()),
                    TabItem(R.string.channels_tab_label, R.drawable.ic_list, ChannelFragment())
                )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val pagerAdapter: AppPagerAdapter = getPagerAdapter()
        binding = FragmentMainCommunityBinding.inflate(inflater)
        binding.communityViewPager.adapter = pagerAdapter
        pagerAdapter.attachTabs(binding.communityTabLayout, binding.communityViewPager)
        return binding.root
    }
}