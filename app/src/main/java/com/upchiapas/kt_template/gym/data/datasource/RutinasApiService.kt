package com.upchiapas.kt_template.gym.data.datasource

import com.upchiapas.kt_template.gym.data.dto.RutinaDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RutinasApiService {

    @GET("rutinas")
    suspend fun getRutinas(): List<RutinaDto>

    @GET("rutinas/{id}")
    suspend fun getRutina(@Path("id") id: Int): RutinaDto

    @POST("rutinas")
    suspend fun createRutina(@Body rutina: RutinaDto): RutinaDto

    @PUT("rutinas/{id}")
    suspend fun updateRutina(@Path("id") id: Int, @Body rutina: RutinaDto): RutinaDto

    @DELETE("rutinas/{id}")
    suspend fun deleteRutina(@Path("id") id: Int)
}