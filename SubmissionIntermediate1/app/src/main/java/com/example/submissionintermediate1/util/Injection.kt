package com.example.submissionintermediate1.util

import android.content.Context
import com.example.submissionintermediate1.data.api.ApiConfig
import com.example.submissionintermediate1.data.response.StoryRepository

object Injection {

    fun provideUserRepository(context: Context): UserRepository {
        val pref = SettingPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.apiInstance
        return UserRepository.getInstance(apiService,pref)
    }

    fun provideStoryRepository(): StoryRepository {
        val apiService = ApiConfig.apiInstance
        return StoryRepository.getInstance(apiService)
    }
}