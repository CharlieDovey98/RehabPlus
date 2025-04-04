package com.charliedovey.rehabplus.viewmodel

// Import necessary libraries for Coroutines and ViewModel.
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
// Project imports.
import com.charliedovey.rehabplus.data.RetrofitInstance
import com.charliedovey.rehabplus.model.User

/**
 * UserViewModel handles the storage of the current user accessing the app and
 * retrieval of user data from the Azure backend.
 * This file is for storing user data as a StateFlow for use in the UI.
 */

class UserViewModel : ViewModel() {

    // Private mutable flow that holds the list of users.
    private val _users = MutableStateFlow<List<User>>(emptyList()) // MutableStateFlow is data stream that the UI can observe.
    val users: StateFlow<List<User>> = _users // Public a read-only version of _users.

    // The apps current signed in user.
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init { // Init is called when the ViewModel is first created, triggering the function to fetch user data from the cloud.
        fetchUsers()
    }

    // A function to fetch users from the Azure backend and update the _users StateFlow with the result.
    private fun fetchUsers() {
        viewModelScope.launch {
            // A try catch to make the network call using Retrofit and update the Stateflow or print the caught error.
            try {
                _users.value = RetrofitInstance.api.getUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // setCurrentUser function called to store the signed-in user.
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    // clearCurrentUser function to sign out the user from the app.
    fun clearCurrentUser() {
        _currentUser.value = null
    }
}
