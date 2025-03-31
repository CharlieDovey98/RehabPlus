package com.charliedovey.rehabplus.data

// Import Moshi for automatic JSON to Kotlin conversion.
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
// Import OkHttp for making network requests and logging.
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
// Import Retrofit for making HTTP requests.
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * RetrofitInstance is a singleton object that sets up and configures Retrofit for network requests.
 * This file connects the app to the cloud based Azure Functions and parses JSON using Moshi.
 */

// This singleton object sets up and provides a configured Retrofit API instance.
object RetrofitInstance {

    // The base URL fro the Azure Function App.
    private const val BASE_URL = "https://rehabplus-functions.azurewebsites.net/api/"

    // Define moshi to handle Kotlin conversion.
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory()).build()

    // Define OkHttpClient with logging to show HTTP request and response details in logcat.
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }).build()

    // Retrofit API lazily created on use not when the app starts.
    val api: AzureApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Connect to Azure Function using the base URL.
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // Convert the JSON into Kotlin style.
            .client(client) // Use the configured OkHttpClient.
            .build() // Construct the Retrofit using the settings above.
            .create(AzureApi::class.java) // Create code implementation for easy calling.
    }
}
