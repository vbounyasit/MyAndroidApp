package com.example.mykotlinapp.ui.screens.chats.history.group.posts.comments

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.ItemUserCommentBinding
import com.example.mykotlinapp.domain.pojo.PayloadType
import com.example.mykotlinapp.model.dto.inputs.ui_item.impl.UpdateCommentVoteInputUI
import com.example.mykotlinapp.model.dto.ui.post.CommentDTO
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter.Companion.PayloadMetaData
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener

class CommentsAdapter(
    val context: Context,
    replyClickListener: ClickListener<CommentDTO>,
    upVoteClickListener: ClickListener<CommentDTO>,
    downVoteClickListener: ClickListener<CommentDTO>,
    commentMenuClickListener: ClickListener<CommentDTO>,
) : AppListAdapter<CommentDTO, ItemUserCommentBinding>(
    bindData = { property, binding ->
        val depth = property.depthLevel - 1
        val depthFlags =
            property.depthFlags.split(context.resources.getString(R.string.profile_pictures_delimiter))
                .map { it.toBoolean() }
        binding.property = property
        binding.replyListener = replyClickListener
        binding.upVoteListener = upVoteClickListener
        binding.downVoteListener = downVoteClickListener
        binding.menuListener = commentMenuClickListener
        binding.userCommentCard.post {
            if (depth < 0) {
                binding.verticalReplyLine.visibility = View.GONE
                binding.verticalNextReplyLine.visibility = View.GONE
                binding.horizontalReplyLine.visibility = View.GONE
            } else {
                val layoutParams = RelativeLayout.LayoutParams(
                    binding.verticalReplyLine.width,
                    binding.verticalReplyLine.height
                )
                val startMargin = context.resources.getDimensionPixelSize(R.dimen.standard_spacing)
                val baseMargin = context.resources.getDimensionPixelSize(R.dimen.small_spacing)
                val marginFactor =
                    context.resources.getDimensionPixelSize(R.dimen.intermediate_spacing)
                var finalMargin = startMargin
                if (depth > 0)
                    for (i in 0 until depth) {
                        if (depthFlags[i]) {
                            //creating reply vertical line
                            val newParams = RelativeLayout.LayoutParams(
                                context.resources.getDimensionPixelSize(R.dimen.reply_line_width),
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                            )
                            newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                            newParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.user_comment_card)
                            newParams.marginStart = finalMargin
                            val newLine = View(context)
                            newLine.background =
                                context.resources.getDrawable(R.color.reply_line, null)
                            newLine.layoutParams = newParams
                            binding.userCommentLayout.addView(newLine)
                        }
                        finalMargin += baseMargin + marginFactor
                    }
                layoutParams.marginStart = finalMargin
                layoutParams.marginEnd = baseMargin
                binding.verticalReplyLine.layoutParams = layoutParams
                if (property.isLastReply) binding.verticalNextReplyLine.visibility = View.GONE
            }
        }
    },
    inflate = { inflater, _ -> ItemUserCommentBinding.inflate(inflater) },
    payloadsMetadata = listOf(
        PayloadMetaData(
            payloadType = PayloadType.PAYLOAD_VOTE_STATE,
            toInputDTO = { UpdateCommentVoteInputUI(it.remoteId, it.voteState, it.votesCount) },
            applyChangesFromPayload = { inputDTO, binding ->
                val input = inputDTO as UpdateCommentVoteInputUI
                binding.property = binding.property?.copy(
                    voteState = input.voteState,
                    votesCount = input.votesCount
                )
            }
        )
    )
)