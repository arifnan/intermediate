package com.example.submissionintermediate1.auth

data class UserModel (
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)