package com.example.mykotlinapp.model.entity.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.ContactRelationType
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.entity.SyncData
import com.example.mykotlinapp.ui.components.recycler_view.RecyclerViewItem

@Entity(tableName = "user_contacts")
data class UserContact(
    @PrimaryKey
    @ColumnInfo(name = "contact_remote_id")
    override val remoteId: String,
    @ColumnInfo(name = "contact_first_name")
    val firstName: String,
    @ColumnInfo(name = "contact_last_name")
    val lastName: String,
    @ColumnInfo(name = "contact_full_name")
    val fullName: String,
    @ColumnInfo(name = "contact_profile_picture")
    val profilePicture: String,
    @ColumnInfo(name = "contact_description")
    val description: String?,
    @ColumnInfo(name = "contact_last_active")
    val lastActive: Long,
    @ColumnInfo(name = "contact_relation_type")
    val relationType: ContactRelationType,
    @ColumnInfo(name = "contact_sync_state")
    override val syncState: SyncState,
) : RecyclerViewItem, SyncData