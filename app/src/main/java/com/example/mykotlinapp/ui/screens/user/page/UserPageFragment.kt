package com.example.mykotlinapp.ui.screens.user.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.mykotlinapp.R
import com.example.mykotlinapp.activities.UserActivityViewModel
import com.example.mykotlinapp.databinding.FragmentUserPageBinding
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.WithViewPager
import com.example.mykotlinapp.ui.components.view_pager.AppPagerAdapter
import com.example.mykotlinapp.ui.components.view_pager.TabItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPageFragment : AppFragment(), WithViewPager {

    private val sharedViewModel: UserActivityViewModel by activityViewModels()

    override fun getPagerAdapter(): AppPagerAdapter {
        return object : AppPagerAdapter(this) {
            override val tabItems: List<TabItem> = listOf(
                TabItem(titleResource = R.string.log_in, fragment = UserLoginFragment()),
                TabItem(titleResource = R.string.sign_up, fragment = UserRegistrationFragment())
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentUserPageBinding.inflate(inflater)
        val pagerAdapter: AppPagerAdapter = getPagerAdapter()
        binding.activityViewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.userPageViewpager.adapter = pagerAdapter
        pagerAdapter.attachTabs(binding.chatWindowTabLayout, binding.userPageViewpager)
        return binding.root
    }
}