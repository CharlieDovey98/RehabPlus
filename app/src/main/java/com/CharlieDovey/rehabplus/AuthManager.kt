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
// Project imports.
import com.charliedovey.rehabplus.model.User

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
    fun signIn(activity: Activity, callback: (User?) -> Unit) {
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
                        .withCallback(getAuthCallback(callback))
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
    private fun getAuthCallback(callback: (User?) -> Unit): AuthenticationCallback {
        return object : AuthenticationCallback {
            // Authentication success function to build the user object from the received token.
            override fun onSuccess(result: IAuthenticationResult) {
                Log.d("AuthManager", "Login success")

                val parts = result.accessToken.split(".")
                var user: User? = null

                if (parts.size == 3) {
                    try {
                        val decoded = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP)
                        val json = JSONObject(String(decoded, Charsets.UTF_8))

                        // Extract wanted fields using the assigned claim names.
                        val givenName = json.optString("given_name", "")
                        val familyName = json.optString("family_name", "")
                        val fullName = "$givenName $familyName".trim()
                        val email = json.optJSONArray("emails")?.optString(0) ?: ""
                        val id = json.optString("oid", "")

                        Log.d("AuthManager", "User: $id $fullName $email $givenName $familyName")

                        // Build the user object from the fields above.
                        user = User(
                            id = id,
                            name = fullName,
                            email = email,
                            completedQuestionnaire = false,
                            assignedProgramIds = emptyList(),
                            earnedBadgeIds = emptyList()
                        )

                    } catch (e: Exception) {
                        Log.e("AuthManager", "Error decoding token: ${e.message}")
                    }
                }
                callback(user)
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