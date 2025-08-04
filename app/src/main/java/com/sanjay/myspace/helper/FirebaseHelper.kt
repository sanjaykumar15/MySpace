package com.sanjay.myspace.helper

import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class FirebaseHelper {

    private val firebaseAuth = Firebase.auth
    val currentUser get() = firebaseAuth.currentUser

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun login(
        username: String,
        password: String,
        onLoginRes: (Boolean, String?, String?) -> Unit,
    ) {
        firebaseAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    onLoginRes(true, user?.uid, null)
                } else {
                    onLoginRes(false, null, task.exception?.localizedMessage)
                }
            }
    }

    fun signUp(
        username: String,
        password: String,
        onSignUpRes: (Boolean, String?, String?) -> Unit,
    ) {
        firebaseAuth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    onSignUpRes(true, user?.uid, null)
                } else {
                    onSignUpRes(false, null, task.exception?.localizedMessage)
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

}