package com.example.submissionintermediate1.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submissionintermediate.auth.UserModel
import com.example.submissionintermediate1.data.response.StoryRepository
import com.example.submissionintermediate1.data.response.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun register(name: String, email: String, password: String) =
        userRepository.register(name, email, password)

    fun login(email: String, password: String) = userRepository.login(email, password)

    fun getSession(): LiveData<UserModel> = userRepository.getLoginSession().asLiveData()

    fun setlogin(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveLoginSession(user)
        }
    }

    fun deleteLogin() {
        viewModelScope.launch {
            userRepository.clearLoginSession()
        }
    }

    fun getStories(token: String) = storyRepository.getStories(token)

    fun addNewStory(token: String, description: String, photo: File) =
        storyRepository.addNewStory(token, description, photo)
}