package com.example.mykotlinapp.ui.screens.chats.history.group.posts.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.work.WorkInfo
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.FragmentGroupPostBinding
import com.example.mykotlinapp.domain.pojo.VoteState
import com.example.mykotlinapp.model.dto.inputs.form.comment.CreateCommentInput
import com.example.mykotlinapp.model.dto.inputs.form.comment.UpdateCommentInput
import com.example.mykotlinapp.model.dto.inputs.form.comment.UpdateCommentVoteInput
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostInput
import com.example.mykotlinapp.model.dto.inputs.form.post.UpdatePostVoteInput
import com.example.mykotlinapp.model.dto.inputs.ui_item.impl.UpdatePostNotificationInputUI
import com.example.mykotlinapp.model.dto.ui.post.CommentDTO
import com.example.mykotlinapp.model.dto.ui.post.PostDTO
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.activities.MainActivityViewModel
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.PostDrawers
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.PostDrawers.DrawerMenusDefinition
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.dialog.CommentCreationSheetFragment
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.dialog.CommentEditSheetFragment
import com.example.mykotlinapp.ui.screens.chats.history.group.posts.dialog.PostEditSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : AppFragment() {

    private val viewModel by viewModels<CommentsViewModel>()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    lateinit var binding: FragmentGroupPostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val args = CommentsFragmentArgs.fromBundle(requireArguments())
        binding = FragmentGroupPostBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.sortingRulesAdapter = dropDownAdapter
        binding.postTopToolbar.chatWindowToolbar.inflateMenu(R.menu.post_top_bar_menu)
        binding.postTopToolbar.chatWindowToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        viewModel.updateGroupRemoteId(args.groupRemoteId)
        viewModel.updatePostRemoteId(args.postRemoteId)
        viewModel.retrieveData(args.groupRemoteId, args.postRemoteId)
        sharedViewModel.hideBottomNav()
        return binding.root
    }

    private val dropDownAdapter by lazy {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sorting_rules,
            R.layout.component_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.component_spinner_item)
        adapter
    }

    /**
     * Dialogs
     */
    private val editPostDialog by lazy {
        PostEditSheetFragment(confirmFunction = {
            viewModel.editPost(
                it
            )
        })
    }
    private val editCommentDialog by lazy {
        CommentEditSheetFragment(confirmFunction = {
            viewModel.editComment(
                it
            )
        })
    }

    private val createCommentDialog by lazy {
        CommentCreationSheetFragment(confirmFunction = {
            viewModel.userPost.value?.let { userPost ->
                viewModel.createComment(userPost.groupRemoteId, userPost.remoteId, it)
            }
        })
    }

    /**
     * Drawer Menus
     */
    private val postDrawerMenus by lazy { postDrawerMenusDefinition.getDrawerMenus() }
    private val commentDrawerMenus by lazy { commentDrawerMenusDefinition.getDrawerMenus() }

    private val postDrawerMenusDefinition by lazy {
        DrawerMenusDefinition(
            requireContext(),
            { sharedViewModel.dialogFormFragmentManager.openDialogForm(editPostDialog.dialogFragmentTag) },
            { viewModel.deletePost(it) },
            { bundle ->
                bundle.getString(getString(R.string.post_remote_id))?.let {
                    viewModel.updateNotifications(
                        UpdatePostNotificationInputUI(
                            it,
                            !bundle.getBoolean(getString(R.string.post_notifications))
                        )
                    )
                }
            },
            PostDrawers.postDrawerTags,
            PostDrawers.postDrawerResources
        )
    }
    private val commentDrawerMenusDefinition by lazy {
        DrawerMenusDefinition(
            requireContext(),
            { sharedViewModel.dialogFormFragmentManager.openDialogForm(editCommentDialog.dialogFragmentTag) },
            { viewModel.deleteComment(it) },
            { bundle -> bundle.getString(getString(R.string.comment_remote_id))?.let { } },//todo
            PostDrawers.commentDrawerTags,
            PostDrawers.commentDrawerResources
        )
    }

    override fun registerUIComponents() {
        super.registerUIComponents()
        sharedViewModel.dialogFormFragmentManager.registerDialogForm(editPostDialog)
        sharedViewModel.dialogFormFragmentManager.registerDialogForm(editCommentDialog)
        sharedViewModel.dialogFormFragmentManager.registerDialogForm(createCommentDialog)
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
        sharedViewModel.bottomDrawerManager.registerNavigationDrawerMenu(
            requireActivity(),
            commentDrawerMenus.drawerMenu
        )
        sharedViewModel.bottomDrawerManager.registerNavigationDrawerMenu(
            requireActivity(),
            commentDrawerMenus.adminDrawerMenu
        )
        sharedViewModel.bottomDrawerManager.registerNavigationDrawerMenu(
            requireActivity(),
            commentDrawerMenus.creatorDrawerMenu
        )
    }

    override fun registerListeners() {
        super.registerListeners()
        binding.postHeader.menuListener = ClickListener<PostDTO> {
            viewModel.userPost.value?.let {
                val isGroupAdmin: Boolean = viewModel.groupData.value?.isAdmin ?: false
                editPostDialog.initialInput = UpdatePostInput(it.remoteId, it.content)
                val drawerMenu =
                    if (it.isCreator) postDrawerMenus.creatorDrawerMenu else if (isGroupAdmin) postDrawerMenus.adminDrawerMenu else postDrawerMenus.drawerMenu
                sharedViewModel.bottomDrawerManager.openDrawer(drawerMenu) {
                    putString(getString(R.string.post_remote_id), it.remoteId)
                }
            }
        }
        binding.postHeader.upVoteListener =
            ClickListener<PostDTO> { viewModel.votePost(getVoteInput(it, VoteState.UP_VOTED)) }
        binding.postHeader.downVoteListener =
            ClickListener<PostDTO> { viewModel.votePost(getVoteInput(it, VoteState.DOWN_VOTED)) }
        binding.postHeader.clickListener = ClickListener<PostDTO> {
            createCommentDialog.initialInput = CreateCommentInput(null, "", it.posterName)
            sharedViewModel.dialogFormFragmentManager.openDialogForm(createCommentDialog.dialogFragmentTag)
        }
    }

    override fun registerObservers() {
        super.registerObservers()
        viewModel.isHeaderPostDeleted.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().onBackPressed()
                viewModel.onHeaderPostDeletion()
            }
        }
        viewModel.commentsAdminPrivilege.observe(viewLifecycleOwner) {
            binding.postComments.adapter = getCommentsAdapter(it.first == true || it.second == true)
        }
        viewModel.shouldReloadAdapter.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.commentsAdminPrivilege.value?.let { privilege ->
                    binding.postComments.adapter =
                        getCommentsAdapter(privilege.first == true || privilege.second == true)
                }
                viewModel.onReloadAdapter()
            }
        }
        viewModel.removeCommentsWorkInfo.observe(viewLifecycleOwner) {
            if (it.isNotEmpty() && it.first().state.isFinished && it.first().state == WorkInfo.State.SUCCEEDED)
                viewModel.onCommentsDeletion()
        }
    }

    private fun getCommentsAdapter(isAdmin: Boolean): CommentsAdapter {
        fun getVoteInput(property: CommentDTO, targetVoteState: VoteState): UpdateCommentVoteInput {
            val resultVoteState: VoteState =
                if (property.voteState == targetVoteState) VoteState.NONE else targetVoteState
            val voteDelta = resultVoteState.value - property.voteState.value
            return UpdateCommentVoteInput(property.remoteId, resultVoteState, voteDelta)
        }
        return CommentsAdapter(
            context = requireContext(),
            replyClickListener = ClickListener {
                createCommentDialog.initialInput =
                    CreateCommentInput(it.remoteId, "", it.commenterName)
                sharedViewModel.dialogFormFragmentManager.openDialogForm(createCommentDialog.dialogFragmentTag)
            },
            upVoteClickListener = ClickListener {
                viewModel.voteComment(
                    getVoteInput(
                        it,
                        VoteState.UP_VOTED
                    )
                )
            },
            downVoteClickListener = ClickListener {
                viewModel.voteComment(
                    getVoteInput(
                        it,
                        VoteState.DOWN_VOTED
                    )
                )
            },
            commentMenuClickListener = ClickListener {
                editCommentDialog.initialInput = UpdateCommentInput(it.remoteId, it.content)
                val drawerMenu =
                    if (it.isCreator) commentDrawerMenus.creatorDrawerMenu else if (isAdmin) commentDrawerMenus.adminDrawerMenu else commentDrawerMenus.drawerMenu
                sharedViewModel.bottomDrawerManager.openDrawer(drawerMenu) {
                    putString(getString(R.string.comment_remote_id), it.remoteId)
                }
            },
        )
    }

    private fun getVoteInput(property: PostDTO, targetVoteState: VoteState): UpdatePostVoteInput {
        val resultVoteState: VoteState =
            if (property.voteState == targetVoteState) VoteState.NONE else targetVoteState
        val voteDelta = resultVoteState.value - property.voteState.value
        return UpdatePostVoteInput(property.remoteId, resultVoteState, voteDelta)
    }
}