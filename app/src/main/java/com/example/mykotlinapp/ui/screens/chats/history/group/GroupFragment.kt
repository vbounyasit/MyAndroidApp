package com.example.mykotlinapp.ui.screens.chats.history.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mykotlinapp.R
import com.example.mykotlinapp.activities.MainActivityViewModel
import com.example.mykotlinapp.databinding.FragmentTabGroupPageBinding
import com.example.mykotlinapp.model.dto.inputs.form.chat.UpdateGroupInput
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.WithViewPager
import com.example.mykotlinapp.ui.components.view_pager.AppPagerAdapter
import com.example.mykotlinapp.ui.components.view_pager.TabItem
import com.example.mykotlinapp.ui.screens.chats.ChatParticipantAdapter
import com.example.mykotlinapp.ui.screens.chats.history.chat.ChatWindowFragmentDirections
import com.example.mykotlinapp.ui.screens.chats.history.group.events.GroupEventsFragment
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.GroupPostsFragment
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.dialog.PostCreationSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupFragment : AppFragment(), WithViewPager {

    private val viewModel by viewModels<GroupViewModel>()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentTabGroupPageBinding
    private lateinit var pagerAdapter: AppPagerAdapter

    private val participantsAdapter by lazy { ChatParticipantAdapter(requireContext().resources.getDimension(R.dimen.group_participant_pic_size).toInt()) }

    private val groupEditDialog by lazy {
        GroupEditDialogFragment(
            confirmFunction = {
                viewModel.sendGroupUpdate(it)
            }
        )
    }

    private val postCreationDialog by lazy {
        PostCreationSheetFragment(
            confirmFunction = {
                viewModel.groupRemoteId.value?.let { remoteId ->
                    viewModel.createGroupPost(remoteId, it)
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTabGroupPageBinding.inflate(inflater)
        pagerAdapter = getPagerAdapter()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.groupParticipantList.adapter = participantsAdapter
        arguments?.getString(getString(R.string.group_remote_id))?.let {
            viewModel.updateGroupRemoteId(it)
        }
        return binding.root
    }

    override fun onResume() {
        viewModel.groupRemoteId.value?.let { viewModel.readGroup(it) }
        super.onResume()
    }

    override fun registerComponents() {
        super.registerComponents()
        sharedViewModel.dialogFormFragmentManager.registerDialogForm(groupEditDialog)
        sharedViewModel.dialogFormFragmentManager.registerDialogForm(postCreationDialog)
    }

    override fun registerListeners() {
        super.registerListeners()
        binding.groupEditButton.setOnClickListener {
            sharedViewModel.dialogFormFragmentManager.openDialogForm(groupEditDialog.dialogFragmentTag)
        }
        binding.postCreationFab.setOnClickListener {
            sharedViewModel.dialogFormFragmentManager.openDialogForm(postCreationDialog.dialogFragmentTag)
        }
    }

    override fun registerObservers() {
        super.registerObservers()
        viewModel.groupRemoteId.observe(viewLifecycleOwner) {
            pagerAdapter.bundle.apply { putString(getString(R.string.group_remote_id), it) }
            binding.groupPageViewPager.adapter = pagerAdapter
            pagerAdapter.attachTabs(binding.groupPageTabLayout, binding.groupPageViewPager)
        }
        viewModel.groupWindow.observe(viewLifecycleOwner) {
            it?.let { groupDTO ->
                groupEditDialog.initialInput = UpdateGroupInput(groupDTO.remoteId, groupDTO.name, groupDTO.description)
            }
        }
        viewModel.navigateToCreatedPostId.observe(viewLifecycleOwner) {
            it?.let { postRemoteId ->
                viewModel.groupRemoteId.value?.let { groupRemoteId ->
                    findNavController().navigate(ChatWindowFragmentDirections.actionGroupChatFragmentToPostFragment(postRemoteId, groupRemoteId))
                    viewModel.onNavigatedToCreatedPost()
                }
            }
        }
    }

    override fun getPagerAdapter(): AppPagerAdapter {
        return object : AppPagerAdapter(this) {
            override val tabItems: List<TabItem>
                get() = listOf(
                    TabItem(titleResource = R.string.posts_label, fragment = attach(GroupPostsFragment(), bundle)),
                    TabItem(titleResource = R.string.event_label, fragment = attach(GroupEventsFragment(), bundle))
                )
        }
    }

}