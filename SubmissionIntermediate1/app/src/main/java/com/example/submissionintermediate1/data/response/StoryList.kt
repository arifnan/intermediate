package com.example.submissionintermediate1.data.response

import com.google.gson.annotations.SerializedName

data class StoryList (

    @field: SerializedName("listStory")
    val listStory: List<Story>,

    @field: SerializedName("error")
    val error: Boolean? = null,

    @field: SerializedName("message")
    val message: String? = null
)