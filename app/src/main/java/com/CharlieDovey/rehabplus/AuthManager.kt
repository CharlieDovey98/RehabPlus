package com.charliedovey.rehabplus

// Import necessary libraries for Android and MSAL.
import android.app.Activity
import android.content.Context
import android.util.Log
import com.microsoft.identity.client.* // Microsoft Authentication Library (MSAL).
import com.microsoft.identity.client.exception.MsalException

/**
 * AuthManager handles user authentication using MSAL.
 * This class takes care of initialising MSAL and launching the sign-in screen, handling the result.
 */
object AuthManager {

    private var msalApp: IMultipleAccountPublicClientApplication? = null

    // Initialises the MSAL application using msal_config.json.
    fun init(context: Context, onReady: () -> Unit) {
        Log.d("AuthManager", "init() called")

        Logger.getInstance().apply {
            setLogLevel(Logger.LogLevel.VERBOSE)
            setEnableLogcatLog(true)
        }
        MultipleAccountPublicClientApplication.create(
            context,
            R.raw.msal_config,
            object : IPublicClientApplication.ApplicationCreatedListener {
                override fun onCreated(application: IPublicClientApplication) {
                    Log.d("AuthManager", "MSAL initialised successfully")
                    msalApp = application as IMultipleAccountPublicClientApplication
                    onReady() // Callback function to say MSAL is ready.
                }

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

        // Check the MSAL APP has been initialised, then acquire the user information declared in Azure and attempt to sign in.
        if (msalApp == null) {
            Log.e("AuthManager", "msalApp is NULL! Did init() fail?")
            callback(null)
            return
        }

        Log.d("AuthManager", "msalApp is NOT null. Starting acquireToken...")
        msalApp!!.acquireToken(
            activity,
            arrayOf("https://graph.microsoft.com/User.Read"),
            object : AuthenticationCallback {
                // Called when authentication is successful.
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    Log.d("AuthManager", "Authentication successful!")

                    callback(authenticationResult.account) // Return the authenticated user's account object.
                }

                // Called when authentication fails due to an exception.
                override fun onError(exception: MsalException) {
                    Log.e("AuthManager", "Authentication error: ${exception.message}")

                    exception.printStackTrace()
                    callback(null) // Return null.
                }

                // Called if the user cancels the sign in process.
                override fun onCancel() {
                    Log.w("AuthManager", "User cancelled sign-in")
                    callback(null)
                }
            }
        )
    }

}

