package com.charliedovey.rehabplus

// Import necessary libraries for Android and MSAL.
import android.app.Activity
import android.content.Context
import android.util.Log
import com.microsoft.identity.client.* // Microsoft Authentication Library (MSAL).
import com.microsoft.identity.client.exception.MsalDeclinedScopeException
import com.microsoft.identity.client.exception.MsalException

/**
 * AuthManager handles user authentication using MSAL.
 * This class takes care of initialising MSAL and launching the sign-in screen, handling the result.
 */
object AuthManager {
    // MSAL application object for single user use.
    private var msalApp: ISingleAccountPublicClientApplication? = null

    // Initialises the MSAL application using msal_config.json.
    fun init(context: Context, onReady: () -> Unit) {
        Log.d("AuthManager", "init() called")

        // Enable MSAL logs for debugging.
        Logger.getInstance().apply {
            setLogLevel(Logger.LogLevel.VERBOSE)
            setEnableLogcatLog(true)
        }
        // Create MSAL client from msal_config.
        PublicClientApplication.createSingleAccountPublicClientApplication(
            context,
            R.raw.msal_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {

                // Called when the MSAL app is created successfully.
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    Log.d("AuthManager", "MSAL initialised successfully")
                    msalApp = application
                    onReady() // Callback function to say MSAL is ready.
                }
                // Called if MSAL creation fails.
                override fun onError(exception: MsalException) {
                    Log.e("AuthManager", "MSAL init failed: ${exception.message}")
                    exception.printStackTrace()
                }
            }
        )
    }

    // Function to start the sign in flow of the user using Azure AD B2C.
    fun signIn(activity: Activity, callback: (IAccount?) -> Unit) {
        Log.d("AuthManager", "signIn() called")
        val app = msalApp
        // A check to see if MSAL was initialised.
        if (app == null) {
            Log.e("AuthManager", "MSAL app not initialised")
            callback(null)
            return
        }

        // A Check to see if a user is already signed in before calling signOut.
        app.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
            // Function to check if there is an active account.
            override fun onAccountLoaded(activeAccount: IAccount?) {
                // If there is an active account, sign out first to restart the process.
                if (activeAccount != null) {
                    Log.d("AuthManager", "Already signed in. Proceeding to sign out first.")
                    app.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                        // Function to call the the user to sign in after a successful sign out.
                        override fun onSignOut() {
                            Log.d("AuthManager", "Signed out for fresh sign-in")
                            startInteractiveSignIn(app, activity, callback)
                        }
                        // Function to log an error that has occurred during sign out.
                        override fun onError(e: MsalException) {
                            Log.e("AuthManager", "Error signing out: ${e.message}")
                            callback(null)
                        }
                    })
                } else { // Else no active account, call the user to sign in.
                    startInteractiveSignIn(app, activity, callback)
                }
            }

            // Not used but required to be present by the interface.
            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
            }

            // Function to log an error that occurred trying to load current account.
            override fun onError(exception: MsalException) {
                Log.e("AuthManager", "Error checking existing account: ${exception.message}")
                callback(null)
            }
        })
    }

    // Function to start interactive sign in.
    private fun startInteractiveSignIn(app: ISingleAccountPublicClientApplication, activity: Activity, callback: (IAccount?) -> Unit) {
        app.acquireToken(
            AcquireTokenParameters.Builder()
                .startAuthorizationFromActivity(activity)
                .withScopes(
                    listOf(
                        "https://rehabplusad.onmicrosoft.com/rehabplus-api/user_access", // RehabPlus custom API scope.
                        "openid", // ID token scope, currently causing errors.
                        "offline_access" // Refresh token scope, currently causing errors.
                    )
                )
                .withCallback(object : AuthenticationCallback {
                    // function called when MSAL Authentication succeeds.
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        val account = authenticationResult.account
                        val idToken = authenticationResult.accessToken

                        Log.d("AuthManager", "Account username: ${account.username}") // Currently returning null.
                        Log.d("AuthManager", "ID Token: $idToken") // Currently returning null.

                        // Attempts to decode the ID token payload for debugging. currently returning null.
                        val parts = idToken.split(".")
                        if (parts.size == 3) {
                            try {
                                val payload = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP)
                                val json = String(payload, Charsets.UTF_8)
                                Log.d("AuthManager", "Decoded ID Token payload: $json")
                            } catch (e: Exception) {
                                Log.e("AuthManager", "Failed to decode ID token: ${e.message}")
                            }
                        }

                        callback(account)
                    }

                    // Function called if authentication fails.
                    override fun onError(exception: MsalException) {
                        Log.e("AuthManager", "Authentication error")

                        // Check to see if some scopes were declined by the user or system.
                        // Currently returning openid and offline_access.
                        if (exception is MsalDeclinedScopeException) {
                            val declined = exception.declinedScopes?.joinToString()
                            Log.e("AuthManager", "Declined scopes: $declined")
                        }

                        Log.e("AuthManager", exception.message ?: "No error message")
                        exception.printStackTrace()
                        callback(null)
                    }

                    // Function called if user cancels the sign in process.
                    override fun onCancel() {
                        Log.w("AuthManager", "User cancelled sign-in")
                        callback(null)
                    }
                })
                .build()
        )
    }
}
