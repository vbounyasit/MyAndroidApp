package com.example.mykotlinapp.ui.screens.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.FragmentMainChatBinding
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.WithViewPager
import com.example.mykotlinapp.ui.activities.MainActivityViewModel
import com.example.mykotlinapp.ui.components.view_pager.AppPagerAdapter
import com.example.mykotlinapp.ui.components.view_pager.TabItem
import com.example.mykotlinapp.ui.screens.chats.contacts.ContactFragment
import com.example.mykotlinapp.ui.screens.chats.creation.ChatCreationDialogFragment
import com.example.mykotlinapp.ui.screens.chats.history.ChatHistoryFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : AppFragment(), WithViewPager {

    private val viewModel by viewModels<ChatViewModel>()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentMainChatBinding

    private val chatCreationDialog by lazy {
        ChatCreationDialogFragment(
            confirmFunction = { viewModel.createNewChat(it) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pagerAdapter: AppPagerAdapter = getPagerAdapter()
        binding = FragmentMainChatBinding.inflate(inflater)
        binding.chatViewPager.adapter = pagerAdapter
        pagerAdapter.attachTabs(binding.chatTabLayout, binding.chatViewPager)
        sharedViewModel.showBottomNav()
        return binding.root
    }

    override fun registerUIComponents() {
        super.registerUIComponents()
        sharedViewModel.dialogFormFragmentManager.registerDialogForm(chatCreationDialog)
    }

    override fun registerListeners() {
        super.registerListeners()
        binding.chatSearchBarInputField.setOnClickListener { viewModel.navigateToContactSearch() }
        binding.chatHistoryToolBar.setNavigationOnClickListener { sharedViewModel.openUserDrawer() }
        binding.chatHistoryToolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.chat_new_convo -> {
                    sharedViewModel.dialogFormFragmentManager.openDialogForm(chatCreationDialog.dialogFragmentTag)
                    true
                }
                else -> false
            }
        }
    }

    override fun registerObservers() {
        super.registerObservers()
        viewModel.navigateToContactSearchPage.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(ChatFragmentDirections.actionChatFragmentToContactSearchFragment())
                viewModel.onNavigateToContactSearch()
            }
        }
        viewModel.navigateToCreatedChatId.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    ChatFragmentDirections.actionChatFragmentToGroupChatFragment(
                        it
                    )
                )
                viewModel.onNavigatedToCreatedChat()
            }
        }
    }

    override fun getPagerAdapter(): AppPagerAdapter {
        return object : AppPagerAdapter(this) {
            override val tabItems: List<TabItem> = listOf(
                TabItem(iconResource = R.drawable.ic_recent, fragment = ChatHistoryFragment()),
                TabItem(iconResource = R.drawable.ic_contact, fragment = ContactFragment())
            )
        }
    }

}