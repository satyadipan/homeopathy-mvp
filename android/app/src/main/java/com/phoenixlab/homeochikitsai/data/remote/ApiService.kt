package com.phoenixlab.homeochikitsai.data.remote

import com.phoenixlab.homeochikitsai.data.model.SymptomRequest
import com.phoenixlab.homeochikitsai.data.model.SymptomResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/symptom")
    suspend fun sendSymptom(@Body request: SymptomRequest): SymptomResponse
}