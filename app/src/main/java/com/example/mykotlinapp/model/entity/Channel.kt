package com.example.mykotlinapp.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem


@Entity(tableName = "followed_channels")
data class Channel(

    @PrimaryKey
    override val remoteId: String,

    @ColumnInfo(name = "profile_picture")
    val channelPicture: String,

    @ColumnInfo(name = "name")
    val channelName: String,

    @ColumnInfo(name = "follower_count")
    val followerCount: String,

    @ColumnInfo(name = "sync_state")
    val syncState: SyncState,

    ) : RecyclerViewItem