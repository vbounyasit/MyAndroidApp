package com.example.mykotlinapp.ui.screens.chats.history.group.posts

import com.example.mykotlinapp.databinding.ItemUserPostBinding
import com.example.mykotlinapp.domain.pojo.PayloadType
import com.example.mykotlinapp.model.dto.inputs.ui_item.impl.UpdatePostVoteInputUI
import com.example.mykotlinapp.model.dto.ui.post.PostDTO
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter.Companion.PayloadMetaData
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener

class PostsAdapter(
    postClickListener: ClickListener<PostDTO>,
    upVoteClickListener: ClickListener<PostDTO>,
    downVoteClickListener: ClickListener<PostDTO>,
    postMenuClickListener: ClickListener<PostDTO>,
) : AppListAdapter<PostDTO, ItemUserPostBinding>(
    bindData = { property, binding ->
        binding.property = property
        binding.isHeader = false
        binding.clickListener = postClickListener
        binding.upVoteListener = upVoteClickListener
        binding.downVoteListener = downVoteClickListener
        binding.menuListener = postMenuClickListener
    },
    inflate = { inflater, _ ->
        ItemUserPostBinding.inflate(inflater)
    },
    payloadsMetadata = listOf(
        PayloadMetaData(
            payloadType = PayloadType.PAYLOAD_VOTE_STATE,
            toInputDTO = { UpdatePostVoteInputUI(it.remoteId, it.voteState, it.votesCount) },
            applyChangesFromPayload = { inputDTO, binding ->
                val input = inputDTO as UpdatePostVoteInputUI
                binding.property = binding.property?.copy(voteState = input.voteState, votesCount = input.votesCount)
            }
        )
        /*Companion.PayloadMetaData(
            payloadType = PayloadType.PAYLOAD_NOTIFICATION_RULE,
            toInputDTO = { UserPostNotification(it.remoteId, it.notificationsEnabled) },
            bindFromPayload = { inputDTO, binding ->
                val notificationsEnabled = (inputDTO as UserPostNotification).notificationEnabled
                binding.property?.let { binding.property = it.copy(notificationsEnabled = notificationsEnabled) }
                binding.userPostNotificationIcon.visibility = if (notificationsEnabled) View.VISIBLE else View.GONE
            }
        )*/
    )
)