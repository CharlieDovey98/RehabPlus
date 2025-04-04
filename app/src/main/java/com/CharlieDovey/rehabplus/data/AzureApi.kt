package com.charliedovey.rehabplus.data

// Retrofit imports to allow for GET, POST requests.
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST
// Project import.
import com.charliedovey.rehabplus.model.User


/**
 * AzureApi defines the endpoints available from the Node.js and Azure Functions backend.
 * Each function in this interface represents a network call that Retrofit will handle
 * and generate code that sends HTTP requests.
 */

// Interface used by Retrofit to generate network code.
interface AzureApi {

    // This function performs a GET request to /getUsers.
    @GET("getUsers")
    suspend fun getUsers(): List<User>

    // This function performs a GET request to /getUserByEmail using a user email query.
    @GET("getUserByEmail")
    suspend fun getUserByEmail(@Query("email") email: String): User

    //This function performs a POST method to /createUser using a body of user information.
    @POST("createUser")
    suspend fun createUser(@Body user: User): User


    // This function performs a GET request to /getPrograms
    // @GET("getPrograms")
    // suspend fun getPrograms(): List<Program>

    // This function performs a GET request to /getExercises
    // @GET("getExercises")
    // suspend fun getExercises(): List<Exercise>


}
