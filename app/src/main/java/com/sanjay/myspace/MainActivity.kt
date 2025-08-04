package com.sanjay.myspace

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.sanjay.myspace.helper.FirebaseHelper
import com.sanjay.myspace.navigation.Login
import com.sanjay.myspace.navigation.MySpaceNav
import com.sanjay.myspace.navigation.MySpaceScreen
import com.sanjay.myspace.ui.component.AlertDialog
import com.sanjay.myspace.ui.theme.MySpaceTheme
import com.sanjay.myspace.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var alertMsg by remember { mutableStateOf("") }

            if (alertMsg.isNotEmpty()) {
                AlertDialog(
                    message = alertMsg,
                    onDismiss = {
                        alertMsg = ""
                    },
                    onConfirm = {
                        alertMsg = ""
                    }
                )
            }

            MySpaceTheme {
                val navController = rememberNavController()
                val viewModel = hiltViewModel<MainViewModel>()
                val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
                val firebaseHelper = FirebaseHelper()
                MySpaceNav(
                    navHostController = navController,
                    initialRoute = if (firebaseHelper.isUserLoggedIn()) MySpaceScreen else Login,
                    isConnected = isConnected,
                    onTerminate = {
                        finish()
                    },
                    showToast = {
                        if (it.length > 20) {
                            alertMsg = it
                        } else {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}