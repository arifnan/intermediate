package com.example.submissionintermediate1.data.response

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.submissionintermediate1.data.api.ApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService
) {
    fun getStories(token: String): LiveData<Result<StoryList?>> {
        val responseLiveData: MutableLiveData<Result<StoryList?>> = MutableLiveData()
        responseLiveData.value = Result.Loading

        try {
            val response = apiService.getStories("Bearer $token").enqueue(object : Callback<StoryList>{
                override fun onResponse(call: Call<StoryList>, response: Response<StoryList>) {
                    if (response.isSuccessful) {
                        responseLiveData.value = Result.Success(response.body())
                    } else {
                        responseLiveData.value = Result.Error(response.message())
                    }                }

                override fun onFailure(call: Call<StoryList>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, Error::class.java)
            responseLiveData.value = Result.Error(errorResponse.message.toString())
        } catch (t: Throwable) {
            responseLiveData.value = Result.Error(t.message.toString())
        }
        return responseLiveData
    }

    fun addNewStory(token: String, description: String, imageFile: File): LiveData<Result<Story?>> {
        val responseLiveData: MutableLiveData<Result<Story?>> = MutableLiveData()
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        responseLiveData.value = Result.Loading

        try {
            apiService.addNewStory("Bearer $token", requestBody, multipartBody).enqueue(object : Callback<Any>{
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        responseLiveData.value = Result.Success(null)
                    } else {
                        responseLiveData.value = Result.Error(response.message())
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    responseLiveData.value = Result.Error(t.message.toString())
                }

            })
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, Error::class.java)
            responseLiveData.value = Result.Error(errorResponse.message.toString())
        }
        return responseLiveData
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}
