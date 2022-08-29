package com.example.mykotlinapp.mappers

import android.content.Context
import com.example.mykotlinapp.R
import com.example.mykotlinapp.domain.pojo.ActivityStatus
import com.example.mykotlinapp.domain.pojo.VoteState
import com.example.mykotlinapp.model.mappers.impl.Utils
import com.example.mykotlinapp.model.mappers.impl.Utils.getFormattedAmount
import com.example.mykotlinapp.model.mappers.impl.Utils.getVoteStateValue
import com.example.mykotlinapp.model.mappers.impl.Utils.toCapitalized
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class UtilsTest : WordSpec({
    val context = mockk<Context>(relaxed = true)
    every { context.resources.getInteger(R.integer.minutes_to_inactive) } returns 5

    "Utils.capitalize" should {
        "return capitalized word" {
            //Given
            val word = "uncapitalized"
            //When
            val result = word.toCapitalized()
            //Then
            result shouldBe "Uncapitalized"
        }
    }

    "Int.toFormattedAmount" When {
        "amount is < 1 000" should {
            "return formatted amount" {
                //Given
                val amount = 999
                //When
                val result = amount.getFormattedAmount(Double::toString)
                //Then
                result shouldBe "999"
            }
        }
        "amount is < 1 000 000" should {
            "return formatted amount" {
                //Given
                val amount = 999_999
                //When
                val result = amount.getFormattedAmount(Double::toString)
                //Then
                result shouldBe "999.999K"
            }
        }
        "amount is > 1 000 000" should {
            "return formatted amount" {
                //Given
                val amount = 1_200_000
                //When
                val result = amount.getFormattedAmount(Double::toString)
                //Then
                result shouldBe "1.2M"
            }
        }
    }

    "Utils.toActivityStatus" When {
        val currentTimeMillis = 15L * 1000 * 60
        "has been inactive for a long time" should {
            "return AWAY activity status" {
                //Given
                val lastActive = 7L * 1000 * 60
                //When
                val activityStatus = Utils.toActivityStatus(context, currentTimeMillis, lastActive)
                //Then
                activityStatus shouldBe ActivityStatus.AWAY
            }
        }
        "has been active" should {
            "return ONLINE activity status" {
                //Given
                val lastActive = 13L * 1000 * 60
                //When
                val activityStatus = Utils.toActivityStatus(context, currentTimeMillis, lastActive)
                //Then
                activityStatus shouldBe ActivityStatus.ONLINE
            }
        }
    }

    "Int.getVoteStateValue" When {
        "is up vote value" should {
            "return the correct VoteState" {
                //Given
                val vote = 1
                //When
                val result = vote.getVoteStateValue()
                //Then
                result shouldBe VoteState.UP_VOTED
            }
        }
        "is down vote value" should {
            "return the correct VoteState" {
                //Given
                val vote = -1
                //When
                val result = vote.getVoteStateValue()
                //Then
                result shouldBe VoteState.DOWN_VOTED
            }
        }
        "is neutral value" should {
            "return the correct VoteState" {
                //Given
                val vote = 0
                //When
                val result = vote.getVoteStateValue()
                //Then
                result shouldBe VoteState.NONE
            }
        }
    }


})