package com.example.mykotlinapp.ui.screens.chats.history.group.events

import androidx.lifecycle.*
import com.example.mykotlinapp.model.dto.ui.group.GroupEventItemDTO
import com.example.mykotlinapp.model.repository.impl.GroupEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupEventsViewModel @Inject constructor(private val groupEventRepository: GroupEventRepository) :
    ViewModel() {

    private val groupId = MutableLiveData<Long>()

    val groupEvents: LiveData<List<GroupEventItemDTO>> =
        groupId.switchMap { groupEventRepository.getGroupEvents(it).asLiveData() }

    fun loadEvents(groupId: Long) {
        this.groupId.value = groupId
    }

    fun onEventClicked(eventItem: GroupEventItemDTO) {
        viewModelScope.launch {
            groupEventRepository.updateEvent(0)//todo
        }
    }
}