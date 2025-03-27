package com.charliedovey.rehabplus.auth

import android.app.Activity
import android.content.Context
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.charliedovey.rehabplus.R

object AuthManager {

    private var msalApp: IPublicClientApplication? = null

    fun init(context: Context) {
        msalApp = PublicClientApplication.create(
            context,
            R.raw.msal_config
        )
    }

    fun signIn(activity: Activity, callback: (IAccount?) -> Unit) {
        msalApp?.let { app ->
            app.acquireToken(
                activity,
                arrayOf("https://graph.microsoft.com/User.Read"),
                object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        callback(authenticationResult.account)
                    }

                    override fun onError(exception: MsalException) {
                        exception.printStackTrace()
                        callback(null)
                    }

                    override fun onCancel() {
                        callback(null)
                    }
                }
            )
        }
    }
}
