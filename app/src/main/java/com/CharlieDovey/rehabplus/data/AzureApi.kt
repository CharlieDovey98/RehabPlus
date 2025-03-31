package com.charliedovey.rehabplus.data

import retrofit2.http.GET
import com.charliedovey.rehabplus.model.User

/**
 * AzureApi defines the endpoints available from the Azure Functions backend.
 * Each function in this interface represents a network call that Retrofit will handle
 * and generate code that sends HTTP requests.
 */

// Interface used by Retrofit to generate network code.
interface AzureApi {

    // This function performs a GET request to /getUsers
    @GET("getUsers")
    suspend fun getUsers(): List<User>

    // This function performs a GET request to: /getPrograms
    // @GET("getPrograms")
    // suspend fun getPrograms(): List<Program>

    // This function performs a GET request to: /getExercises
    // @GET("getExercises")
    // suspend fun getExercises(): List<Exercise>


}
