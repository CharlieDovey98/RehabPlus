package com.charliedovey.rehabplus.viewmodel

// Import necessary libraries for Coroutines and ViewModel.
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
// Project imports.
import com.charliedovey.rehabplus.data.RetrofitInstance
import com.charliedovey.rehabplus.model.*

/**
 * UserViewModel handles the storage of the current user accessing the app and
 * retrieval of user data from the Azure backend.
 * This file is for storing user data as a StateFlow for use in the UI.
 */

class UserViewModel : ViewModel() {

    // Private mutable flow that holds the list of users.
    private val _users = MutableStateFlow<List<User>>(emptyList()) // MutableStateFlow is data stream that the UI can observe.
    val users: StateFlow<List<User>> = _users // a Public read only version of _users.

    // The apps current signed in user.
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // The logged in users assigned program.
    private val _assignedProgram: MutableStateFlow<Program?> = MutableStateFlow<Program?>(null)
    val assignedProgram = _assignedProgram.asStateFlow()

    // A map of the list of exercises stored in the database. Mapped with the exercise id for fast lookup.
    private val _exerciseMap = MutableStateFlow<Map<String, Exercise>>(emptyMap())
    val exerciseMap = _exerciseMap.asStateFlow()

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

    // setCurrentUser function called to store the signed in user to the ViewModel.
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    // clearCurrentUser function to sign out the user from the ViewModel.
    fun clearCurrentUser() {
        _currentUser.value = null
    }

    // setAssignedProgram function to set the assigned program of the user in the ViewModel.
    fun setAssignedProgram(program: Program) {
        _assignedProgram.value = program
    }

    // clearAssignedProgram clears the user specific assigned program.
    fun clearAssignedProgram() {
        _assignedProgram.value = null
    }

    // setExerciseMap sets the variable with a map of the exercises by their id.
    fun setExerciseMap(map: Map<String, Exercise>) {
        _exerciseMap.value = map
    }

}
