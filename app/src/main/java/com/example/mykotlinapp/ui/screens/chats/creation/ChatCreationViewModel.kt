package com.example.mykotlinapp.ui.screens.chats.creation

import androidx.lifecycle.*
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.dto.ui.user.UserContactSelectionDTO
import com.example.mykotlinapp.model.repository.impl.UserRepository
import com.example.mykotlinapp.ui.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ChatCreationViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : AppViewModel() {

    private val _selectedContacts = MutableLiveData<List<UserContactDTO>>()
    private val _onMessagePage = MutableLiveData<Boolean>()
    private val _searchTag = MutableLiveData<String>()

    val selectedContacts: LiveData<List<UserContactDTO>> = _selectedContacts
    val onMessagePage: LiveData<Boolean> = _onMessagePage

    val contacts: LiveData<List<UserContactSelectionDTO>> = selectedContacts.switchMap { selectedUsers ->
        userRepository.getUserContacts().map { retrievedContacts ->
            retrievedContacts.map { contact -> UserContactSelectionDTO(contact, selectedUsers.contains(contact)) }
        }.asLiveData()
    }

    val searchedContacts: LiveData<List<UserContactSelectionDTO>> = _searchTag.switchMap { tag ->
        contacts.map {
            if (tag.isNotBlank()) it.filter { contact -> contact.dto.firstName.startsWith(tag) || contact.dto.lastName.startsWith(tag) }
            else it
        }
    }

    init {
        resetCreationForm()
    }

    fun hasSelectedContacts(): Boolean {
        return _selectedContacts.value?.isNotEmpty() ?: false
    }

    fun updateContactSelection(contact: UserContactDTO, isSelected: Boolean) {
        _selectedContacts.value = _selectedContacts.value?.let {
            if (isSelected) it + contact
            else it.filter { selectedContact -> selectedContact.remoteId != contact.remoteId }
        }
    }

    fun updateSearchTag(tag: String) {
        _searchTag.value = tag
    }

    fun toggleOnMessagePage() {
        _onMessagePage.value = _onMessagePage.value?.let { !it }
    }

    private fun resetCreationForm() {
        _searchTag.value = ""
        _onMessagePage.value = false
        _selectedContacts.value = listOf()
    }

}