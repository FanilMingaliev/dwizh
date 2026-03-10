package com.example.authapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.model.UserProfile
import com.example.authapp.data.profile.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.profile.collect { profile ->
                _uiState.value = _uiState.value.copy(
                    fullName = profile.fullName,
                    interests = profile.interests,
                    birthDate = profile.birthDate,
                    gender = profile.gender,
                    aboutMe = profile.aboutMe,
                    facts = profile.facts
                )
            }
        }
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value, message = null)
    }

    fun onInterestsChange(value: String) {
        _uiState.value = _uiState.value.copy(interests = value, message = null)
    }

    fun save() {
        val state = _uiState.value
        repository.updateProfile(
            UserProfile(
                fullName = state.fullName.trim(),
                interests = state.interests.trim(),
                birthDate = state.birthDate,
                gender = state.gender,
                aboutMe = state.aboutMe,
                facts = state.facts
            )
        )
        _uiState.value = _uiState.value.copy(message = "Profile saved")
    }

    fun updateAboutMe(value: String) {
        _uiState.value = _uiState.value.copy(aboutMe = value, message = null)
        repository.updateProfile(
            UserProfile(
                fullName = _uiState.value.fullName,
                interests = _uiState.value.interests,
                birthDate = _uiState.value.birthDate,
                gender = _uiState.value.gender,
                aboutMe = value,
                facts = _uiState.value.facts
            )
        )
    }

    fun updateFact(index: Int, value: String) {
        val list = _uiState.value.facts.toMutableList()
        while (list.size < 5) list.add("")
        list[index] = value
        _uiState.value = _uiState.value.copy(facts = list, message = null)
        repository.updateProfile(
            UserProfile(
                fullName = _uiState.value.fullName,
                interests = _uiState.value.interests,
                birthDate = _uiState.value.birthDate,
                gender = _uiState.value.gender,
                aboutMe = _uiState.value.aboutMe,
                facts = list
            )
        )
    }
}
