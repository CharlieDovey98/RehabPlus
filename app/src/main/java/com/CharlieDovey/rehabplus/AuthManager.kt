package com.charliedovey.rehabplus

// Import necessary libraries for MSAL and Android.
import android.app.Activity
import android.content.Context
import android.util.Base64
import android.util.Log
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalDeclinedScopeException
import com.microsoft.identity.client.exception.MsalException
import org.json.JSONObject
import kotlinx.coroutines.*
// Project imports.
import com.charliedovey.rehabplus.model.User
import com.charliedovey.rehabplus.data.RetrofitInstance
import com.charliedovey.rehabplus.data.userProgram.fetchFullAssignedProgram
import com.charliedovey.rehabplus.viewmodel.UserViewModel

/**
 * AuthManager handles MSAL authentication logic for the RehabPlus app.
 * This file uses Multiple Account mode with Azure AD B2C to sign in and decode user data for RehabPlus.
 */

object AuthManager {
    private var msalApp: IMultipleAccountPublicClientApplication? = null

    // B2C user flow policy name in use.
    private const val B2C_POLICY = "B2C_1_signupsignin" // B2C_1_SignIn

    private val SCOPES = listOf( // Custom user_access scope.
        "https://rehabplusad.onmicrosoft.com/0c17a826-a9b8-4cfd-a3d9-97a51603f183/user_access"
    ) // Removed openid and offline_access due to errors with the scopes.

    // Initialise the MSAL library in multiple account mode.
    fun init(context: Context, onReady: () -> Unit) {
        Log.d("AuthManager", "Initialising MSAL")

        Logger.getInstance().apply {
            setLogLevel(Logger.LogLevel.VERBOSE)
            setEnableLogcatLog(true)
        }

        // Initialise MSAL in multiple-account mode.
        PublicClientApplication.createMultipleAccountPublicClientApplication(
            context,
            R.raw.msal_config, // Use the msal_config.json file for necessary configuration.
            object : IPublicClientApplication.IMultipleAccountApplicationCreatedListener {
                override fun onCreated(application: IMultipleAccountPublicClientApplication) {
                    Log.d("AuthManager", "MSAL initialised successfully")
                    msalApp = application
                    onReady()
                }

                // onError function needed for debugging.
                override fun onError(exception: MsalException) {
                    Log.e("AuthManager", "MSAL init failed: ${exception.message}")
                    exception.printStackTrace()
                }
            }
        )
    }

    // Function to call interactive sign in and return  a populated User object.
    fun signIn(activity: Activity, userViewModel: UserViewModel,callback: (User?) -> Unit) {
        val app = msalApp
        if (app == null) {
            Log.e("AuthManager", "MSAL not initialised")
            callback(null)
            return
        }

        // Debug and clear any signed in users.
        app.getAccounts(object : IPublicClientApplication.LoadAccountsCallback {
            override fun onTaskCompleted(accounts: List<IAccount>) {
                Log.d("AuthManager", "Accounts before sign-in: ${accounts.size}")
                if (accounts.isNotEmpty()) {
                    for (account in accounts) {
                        app.removeAccount(account)
                    }
                }
                Log.d("AuthManager", "Launching interactive sign-in with policy: $B2C_POLICY")

                // Acquire token with B2C authority and scopes list.
                app.acquireToken(
                    AcquireTokenParameters.Builder()
                        .startAuthorizationFromActivity(activity)
                        .withScopes(SCOPES)
                        .fromAuthority("https://rehabplusad.b2clogin.com/tfp/rehabplusad.onmicrosoft.com/$B2C_POLICY")
                        .withCallback(getAuthCallback(userViewModel, callback))
                        .build()
                )
            }

            // Error function with logging for debugging.
            override fun onError(exception: MsalException) {
                Log.e("AuthManager", "Error loading accounts: ${exception.message}")
                callback(null)
            }
        })
    }

    // Callback function used when MSAL login finishes.
    private fun getAuthCallback(userViewModel: UserViewModel, callback: (User?) -> Unit ): AuthenticationCallback {
        return object : AuthenticationCallback {

            // Authentication success function to build the user object from the received token and find or create a new user to store in the Cosmos DB.
            override fun onSuccess(result: IAuthenticationResult) {
                Log.d("AuthManager", "Login success")

                // Split the access token into 3 parts by '.' for header, payload and signature.
                val parts = result.accessToken.split(".")
                var user: User? = null

                // Ensure the token is in the expected format, split from above.
                if (parts.size == 3) {
                    try {
                        // Decode the payload which contains user info needed for RehabPlus.
                        val decoded = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP)
                        val json = JSONObject(String(decoded, Charsets.UTF_8))

                        // Extract wanted fields using the assigned claim names.
                        val givenName = json.optString("given_name", "")
                        val familyName = json.optString("family_name", "")
                        val fullName = "$givenName $familyName".trim()
                        val email = json.optJSONArray("emails")?.optString(0) ?: ""
                        val id = json.optString("oid", "")

                        Log.d("AuthManager", "Decoded User: $id $fullName $email")

                        // Build the user object from the fields above and other needed fields.
                        user = User(
                            id = id,
                            name = fullName,
                            email = email,
                            completedQuestionnaire = false,
                            assignedProgramId = "",
                            earnedBadgeIds = emptyList()
                        )

                    } catch (e: Exception) {
                        Log.e("AuthManager", "Error decoding token: ${e.message}")
                    }
                }

                // If decoding fails exit with null.
                if (user == null) {
                    Log.e("AuthManager", "Failed to decode user from token")
                    callback(null)
                    return
                }

                // Use coroutine to contact the Azure backend asynchronously, off the main thread.
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val api = RetrofitInstance.api // Access the Azure Function API via Retrofit.

                        // Try to fetch the user by email from Cosmos DB.
                        val existingUser = try {
                            api.getUserByEmail(user.email)
                        } catch (e: retrofit2.HttpException) {
                            // If error code 404 is received, a new user needs to be created.
                            if (e.code() == 404) {
                                Log.w("AuthManager","User not found, creating a new user to store in the database.")
                                null
                            } else {
                                throw e // If any other error is received, throw it to outer onError function.
                            }
                        }

                        // Use existing user if found, otherwise create a new user and add to the database.
                        val completedUser = if (existingUser != null) {
                            Log.d("AuthManager", "User exists in database: ${existingUser.email}")
                            existingUser
                        } else {
                            Log.d("AuthManager", "Creating user and adding them to the database.")
                            api.createUser(user)
                        }
                        // If the user has been assigned a program, fetch the program details from the database.
                        val fullProgram = if (completedUser.assignedProgramId.isNotEmpty()) {
                            fetchFullAssignedProgram(completedUser)
                        } else {
                            null // If the user has no assigned program return null.
                        }

                        // Set the full program to ViewModel or handle it in the UI callback
                        withContext(Dispatchers.Main) {
                                fullProgram?.let { (program, exerciseMap) ->
                                    userViewModel.setAssignedProgram(program) // Call setAssignedProgram to update the users program in the userViewModel.
                                    userViewModel.setExerciseMap(exerciseMap)// Call setExerciseMap to update the users exercise list in the userViewModel.
                                }

                            callback(completedUser) // Still return the user back to the UI
                        }

                        // Return completedUser back to the UI main thread after completion.
                        withContext(Dispatchers.Main) {
                            callback(completedUser)
                        }

                    } catch (e: Exception) {
                        // Catch any errors and log them for debugging.
                        Log.e("AuthManager", "Failed to query/create user: ${e.message}")
                        withContext(Dispatchers.Main) {
                            callback(user) /// Return the payload decoded user back to the UI main thread after completion to prevent app crashing.
                        }
                    }
                }

            }

            // onError function to log the authentication error and declined scopes for debugging.
            override fun onError(e: MsalException) {
                Log.e("AuthManager", "Authentication failed: ${e.message}")
                if (e is MsalDeclinedScopeException) {
                    Log.e("AuthManager", "Declined scopes: ${e.declinedScopes?.joinToString()}")
                }
                callback(null)
            }

            // onCancel function to handle the user cancelling the sign in process.
            override fun onCancel() {
                Log.w("AuthManager", "User cancelled sign-in")
                callback(null)
            }
        }
    }


    // signOut function which signs the user out and clears stored account info.
    fun signOut(callback: () -> Unit) {
        Log.d("AuthManager", "Sign out triggered")
        val app = msalApp
        if (app == null) { // If statement to check if MSAL was initialised.
            Log.e("AuthManager", "MSAL not initialised.")
            callback() // Callback to continue other sign out processes.
            return // Exit the signOut function to prevent the app crashing or hanging.
        }

        app.getAccounts(object :
            IPublicClientApplication.LoadAccountsCallback { // Retrieve the accounts from the MSAL application.
            override fun onTaskCompleted(accounts: List<IAccount>) {
                if (accounts.isEmpty()) { // If there are no accounts, there is nothing to remove.
                    Log.d("AuthManager", "No accounts found in MSAL that need to be removed.")
                    callback()
                    return
                }

                var remaining = accounts.size // Attain the number of accounts in MSAL.

                for (account in accounts) { // For loop to remove all accounts within MSAl.
                    app.removeAccount(
                        account,
                        object : IMultipleAccountPublicClientApplication.RemoveAccountCallback {
                            override fun onRemoved() {
                                Log.d("AuthManager", "Removed account: ${account.username}")
                                remaining-- // Once the account is removed, reduce remaining by one.
                                if (remaining == 0) { // If remaining is equal to zero, all accounts have been removed successfully.
                                    Log.d("AuthManager", "All accounts removed successfully.")
                                    callback()
                                }
                            }

                            override fun onError(exception: MsalException) { // For errors removing accounts in MSAL, log the account error and reduce remaining.
                                Log.e("AuthManager", "Error removing account: ${exception.message}")
                                remaining--
                                if (remaining == 0) {
                                    callback()
                                }
                            }
                        })
                }
            }

            override fun onError(exception: MsalException) {
                Log.e("AuthManager", "Error getting accounts within MSAL: ${exception.message}")
                callback()
            }
        })
    }

}