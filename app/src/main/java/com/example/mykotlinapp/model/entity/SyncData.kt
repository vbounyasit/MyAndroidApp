package com.example.mykotlinapp.model.entity

import com.example.mykotlinapp.domain.pojo.SyncState

interface SyncData {
    val remoteId: String
    val syncState: SyncState
}