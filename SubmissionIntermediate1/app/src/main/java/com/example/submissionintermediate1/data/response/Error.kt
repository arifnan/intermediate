package com.example.submissionintermediate1.data.response

import com.google.gson.annotations.SerializedName

data class Error(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message:String
)