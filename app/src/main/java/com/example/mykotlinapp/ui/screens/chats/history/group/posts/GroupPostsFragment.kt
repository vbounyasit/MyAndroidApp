package com.example.mykotlinapp.ui.screens.chats.history.group.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.FragmentTabGroupPostListBinding
import com.example.mykotlinapp.domain.pojo.VoteState
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostInput
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostVoteInput
import com.example.mykotlinapp.model.dto.ui.post.PostDTO
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.activities.MainActivityViewModel
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener
import com.example.mykotlinapp.ui.screens.chats.history.chat.ChatWindowFragmentDirections
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.PostDrawers.DrawerMenusDefinition
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.dialog.PostEditSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupPostsFragment : AppFragment() {

    private val viewModel by viewModels<GroupPostsViewModel>()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentTabGroupPostListBinding

    private val editPostDialog by lazy {
        PostEditSheetFragment(confirmFunction = { viewModel.editPost(it) })
    }

    private val postDrawerMenusDefinition by lazy {
        DrawerMenusDefinition(
            requireContext(),
            { sharedViewModel.dialogFormFragmentManager.openDialogForm(editPostDialog.dialogFragmentTag) },
            { viewModel.deletePost(it) },
            { }, //todo
            PostDrawers.postDrawerTags,
            PostDrawers.postDrawerResources
        )
    }

    private val postDrawerMenus by lazy { postDrawerMenusDefinition.getDrawerMenus() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTabGroupPostListBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        arguments?.getString(getString(R.string.group_remote_id))
            ?.let { viewModel.updateGroupRemoteId(it) }
        return binding.root
    }

    override fun registerUIComponents() {
        super.registerUIComponents()
        sharedViewModel.dialogFormFragmentManager.registerDialogForm(editPostDialog)
        sharedViewModel.bottomDrawerManager.registerNavigationDrawerMenu(
            requireActivity(),
            postDrawerMenus.drawerMenu
        )
        sharedViewModel.bottomDrawerManager.registerNavigationDrawerMenu(
            requireActivity(),
            postDrawerMenus.adminDrawerMenu
        )
        sharedViewModel.bottomDrawerManager.registerNavigationDrawerMenu(
            requireActivity(),
            postDrawerMenus.creatorDrawerMenu
        )
    }

    override fun registerObservers() {
        super.registerObservers()
        viewModel.groupRemoteId.observe(viewLifecycleOwner) {
            viewModel.retrieveData(it)
        }
        viewModel.isGroupAdmin.observe(viewLifecycleOwner) {
            binding.groupPostsList.itemsRecyclerView.adapter = getPostAdapter(it)
        }
        viewModel.navigateToUserPostId.observe(viewLifecycleOwner) {
            it?.let { postRemoteId ->
                arguments?.getString(getString(R.string.group_remote_id))?.let { groupRemoteId ->
                    findNavController().navigate(
                        ChatWindowFragmentDirections.actionGroupChatFragmentToPostFragment(
                            postRemoteId,
                            groupRemoteId
                        )
                    )
                    viewModel.onNavigatedToUserPost()
                }
            }
        }
    }

    private fun getPostAdapter(isGroupAdmin: Boolean): PostsAdapter {
        fun getVoteInput(property: PostDTO, targetVoteState: VoteState): UpdatePostVoteInput {
            val resultVoteState: VoteState =
                if (property.voteState == targetVoteState) VoteState.NONE else targetVoteState
            val voteDelta = resultVoteState.value - property.voteState.value
            return UpdatePostVoteInput(property.remoteId, resultVoteState, voteDelta)
        }
        return PostsAdapter(
            postClickListener = ClickListener { viewModel.navigateToPost(it.remoteId) },
            upVoteClickListener = ClickListener {
                viewModel.votePost(
                    getVoteInput(
                        it,
                        VoteState.UP_VOTED
                    )
                )
            },
            downVoteClickListener = ClickListener {
                viewModel.votePost(
                    getVoteInput(
                        it,
                        VoteState.DOWN_VOTED
                    )
                )
            },
            postMenuClickListener = ClickListener {
                editPostDialog.initialInput = UpdatePostInput(it.remoteId, it.content)
                val drawerMenu =
                    if (it.isCreator) postDrawerMenus.creatorDrawerMenu else if (isGroupAdmin) postDrawerMenus.adminDrawerMenu else postDrawerMenus.drawerMenu
                sharedViewModel.bottomDrawerManager.openDrawer(drawerMenu) {
                    putString(getString(R.string.post_remote_id), it.remoteId)
                }
            }
        )
    }
}
