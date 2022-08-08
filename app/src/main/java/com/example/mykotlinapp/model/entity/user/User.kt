package com.example.mykotlinapp.model.entity.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mykotlinapp.domain.pojo.Gender
import com.example.mykotlinapp.domain.pojo.SyncState

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "remote_id")
    val remoteId: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    @ColumnInfo(name = "profile_picture")
    val profilePicture: String,
    @ColumnInfo(name = "profile_background_picture")
    val profileBackgroundPicture: String?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "gender")
    val gender: Gender,
    @ColumnInfo(name = "age")
    val age: Int,
    @ColumnInfo(name = "sync_state")
    val syncState: SyncState,
)