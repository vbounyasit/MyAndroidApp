package com.example.mykotlinapp.ui.screens.chats.history.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.mykotlinapp.R
import com.example.mykotlinapp.ui.activities.MainActivityViewModel
import com.example.mykotlinapp.databinding.FragmentChatWindowBinding
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.WithViewPager
import com.example.mykotlinapp.ui.components.view_pager.AppPagerAdapter
import com.example.mykotlinapp.ui.components.view_pager.TabItem
import com.example.mykotlinapp.ui.screens.chats.history.chat.logs.ChatLogsFragment
import com.example.mykotlinapp.ui.screens.chats.history.chat.page.ChatPageDialogFragment
import com.example.mykotlinapp.ui.screens.chats.history.group.GroupFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatWindowFragment : AppFragment(), WithViewPager {

    private val viewModel by viewModels<ChatWindowViewModel>()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentChatWindowBinding
    private lateinit var pagerAdapter: AppPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val args = ChatWindowFragmentArgs.fromBundle(requireArguments())
        binding = FragmentChatWindowBinding.inflate(inflater)
        pagerAdapter = getPagerAdapter()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.chatTopToolbar.chatWindowToolbar.inflateMenu(R.menu.chat_window_top_bar_menu)
        viewModel.updateChatRemoteId(args.chatRemoteId)
        sharedViewModel.hideBottomNav()
        return binding.root
    }

    override fun registerListeners() {
        super.registerListeners()
        binding.chatTopToolbar.chatWindowToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.chatTopToolbar.chatWindowToolbar.setOnClickListener {
            viewModel.chatRemoteId.value?.let {
                ChatPageDialogFragment(it).show(childFragmentManager, ChatPageDialogFragment.TAG)
            }
        }
        binding.chatWindowViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.tabSelected = position
                super.onPageSelected(position)
            }
        })
    }

    override fun registerObservers() {
        super.registerObservers()
        viewModel.chatRemoteId.observe(viewLifecycleOwner) {
            viewModel.retrieveChatData(it)
            viewModel.retrieveChatLogs(it)
            sharedViewModel.notificationComponent.setCurrentChat(it)
        }
        viewModel.chatMetaData.observe(viewLifecycleOwner) {
            it?.let {
                pagerAdapter.bundle.apply {
                    putString(getString(R.string.chat_remote_id), it.remoteId)
                    putString(getString(R.string.group_remote_id), it.groupRemoteId)
                    putBoolean(getString(R.string.is_group_chat), it.isGroupChat)
                }
                binding.chatWindowViewPager.adapter = pagerAdapter
                pagerAdapter.attachTabs(binding.chatWindowTabLayout, binding.chatWindowViewPager)
                viewModel.tabSelected?.let { tab ->
                    binding.chatWindowViewPager.setCurrentItem(tab, false)
                }
            }
        }
        viewModel.unreadChatLogsCount.observe(viewLifecycleOwner) {
            pagerAdapter.setBadgeAmount(0, it)
        }
        viewModel.unreadPostsCount.observe(viewLifecycleOwner) {
            pagerAdapter.setBadgeAmount(1, it)
        }
        viewModel.groupRemoteId.observe(viewLifecycleOwner) {
            it?.let { viewModel.retrievePosts(it) }
        }
    }

    override fun getPagerAdapter(): AppPagerAdapter {
        return object : AppPagerAdapter(this) {
            override val tabItems: List<TabItem> = listOf(
                TabItem(iconResource = R.drawable.ic_chat_bubble, fragment = attach(ChatLogsFragment(), bundle), badgeNumber = 0),
                TabItem(iconResource = R.drawable.ic_groups, fragment = attach(GroupFragment(), bundle), badgeNumber = 0)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.chatRemoteId.value?.let(sharedViewModel.notificationComponent::setCurrentChat)
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.notificationComponent.resetCurrentChat()
    }

}