package com.example.sosapp.remote

import com.example.sosapp.model.SOSRequestBody
import com.example.sosapp.util.Resource
import retrofit2.http.Body
import retrofit2.http.GET

interface SOSApi {

    @GET("api/v1/create")
    suspend fun sendSOSAlert(
        @Body body: SOSRequestBody
    ): Resource<String>
}