package com.charliedovey.rehabplus.data

// Retrofit imports to allow for GET, POST requests.
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST
// Project import.
import com.charliedovey.rehabplus.model.*
import retrofit2.Response


/**
 * AzureApi defines the endpoints available from the Node.js and Azure Functions backend.
 * Each function in this interface represents a network call that Retrofit will handle
 * and generate code that sends HTTP requests.
 */

// Interface used by Retrofit to generate network code.
interface AzureApi {

    // This function performs a GET request to /getUsers, returning a list of users in the database.
    @GET("getUsers")
    suspend fun getUsers(): List<User>

    // This function performs a GET request to /getUserByEmail using a user email query, returning a specific user.
    @GET("getUserByEmail")
    suspend fun getUserByEmail(@Query("email") email: String): User

    //This function performs a POST method to /createUser using a body of user information, storing a user in the database.
    @POST("createUser")
    suspend fun createUser(@Body user: User): User


    // This function performs a GET request to /getPrograms, returning a .
    @GET("getProgramById")
    suspend fun getProgramById(@Query("id") id: String): Program

    // This function performs a GET request to /getExercises
    @GET("getExercises")
    suspend fun getExercises(): List<Exercise>

    @POST("createQuestionnaire")
    suspend fun submitQuestionnaire(@Body questionnaire: Questionnaire): Response<Unit>

    @POST("markQuestionnaireComplete")
    suspend fun markQuestionnaireComplete(@Body payload: Map<String, String>): Response<Unit>

}
