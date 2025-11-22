package com.sp45.pocketchef.domain.repository

import kotlinx.coroutines.flow.Flow
import com.sp45.pocketchef.domain.AuthResult
import com.sp45.pocketchef.util.Resource

interface AuthRepository {
    fun loginUser(email: String, password: String): Flow<AuthResult>
    fun registerUser(email: String, password: String): Flow<AuthResult>
    fun getCurrentUser(): Flow<Boolean>
    fun logout(): Flow<Resource<Boolean>>
    fun resetPassword(email: String): Flow<AuthResult>
}