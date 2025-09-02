package com.sanjay.myspace.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sanjay.myspace.ui.event.CardDetailsEvents
import com.sanjay.myspace.ui.event.LoginEvents
import com.sanjay.myspace.ui.event.MySpaceEvents
import com.sanjay.myspace.ui.event.SpaceDetailsEvents
import com.sanjay.myspace.ui.screens.CardDetailsScreen
import com.sanjay.myspace.ui.screens.LoginScreen
import com.sanjay.myspace.ui.screens.MySpaceDetailsScreen
import com.sanjay.myspace.ui.screens.MySpaceScreen
import com.sanjay.myspace.ui.viewmodel.CardDetailsVM
import com.sanjay.myspace.ui.viewmodel.LoginViewModel
import com.sanjay.myspace.ui.viewmodel.MySpaceVM
import com.sanjay.myspace.ui.viewmodel.SpaceDetailsVM
import com.sanjay.myspace.utils.RefreshEventHandler
import com.sanjay.myspace.utils.RefreshEventItem
import com.sanjay.myspace.utils.ShareScreenShot
import kotlinx.coroutines.launch

@Composable
fun MySpaceNav(
    navHostController: NavHostController,
    initialRoute: Any,
    isConnected: Boolean,
    onTerminate: () -> Unit,
    showToast: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val refreshFlow = RefreshEventHandler.eventFlow
        .collectAsStateWithLifecycle(
            initialValue = null,
            context = scope.coroutineContext
        )

    NavHost(
        navController = navHostController,
        startDestination = initialRoute
    ) {
        composable<Login> {
            val viewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                state = viewModel.state,
                onEvent = { event ->
                    when (event) {
                        is LoginEvents.OnLoginClicked -> {
                            val validation = viewModel.validation()
                            when {
                                validation.isEmpty() -> {
                                    if (!isConnected) {
                                        showToast("No Internet Connection")
                                        return@LoginScreen
                                    }
                                    if (!viewModel.state.isSignUp) {
                                        viewModel.login { result, message ->
                                            viewModel.eventHandler(LoginEvents.DismissLoading)
                                            if (result) {
                                                navHostController.navigate(MySpaceScreen)
                                            } else {
                                                showToast(message ?: "Failed to login")
                                            }
                                        }
                                    } else {
                                        viewModel.signUp { result, message ->
                                            viewModel.eventHandler(LoginEvents.DismissLoading)
                                            if (result) {
                                                navHostController.navigate(MySpaceScreen)
                                            } else {
                                                showToast(message ?: "Failed to create account")
                                            }
                                        }
                                    }
                                }

                                else -> {
                                    showToast(validation)
                                }
                            }
                        }

                        is LoginEvents.OnBackPressed -> {
                            onTerminate()
                        }

                        else -> {
                            viewModel.eventHandler(event)
                        }
                    }
                }
            )
        }

        composable<MySpaceScreen> {
            val viewModel = hiltViewModel<MySpaceVM>()
            if (refreshFlow.value == RefreshEventItem.REFRESH_SPACE) {
                viewModel.eventHandler(MySpaceEvents.OnRefresh)
                scope.launch {
                    RefreshEventHandler.eventFlow.emit(null)
                }
            }

            MySpaceScreen(
                state = viewModel.state,
                onEvent = { event ->
                    when (event) {
                        MySpaceEvents.OnBackClicked -> {
                            onTerminate()
                        }

                        MySpaceEvents.OnLogoutClicked -> {
                            viewModel.logout()
                            navHostController.navigate(Login)
                        }

                        is MySpaceEvents.OnItemClicked -> {
                            viewModel.eventHandler(event.copy(isLongClicked = event.isLongClicked || viewModel.state.mySpaces.any { it.isSelected }))
                        }

                        is MySpaceEvents.OnCreateClicked -> {
                            val validationMsg = viewModel.spaceNameValidation()
                            if (validationMsg.isNotEmpty()) {
                                showToast(validationMsg)
                            } else {
                                viewModel.eventHandler(event)
                            }
                        }

                        else -> {
                            viewModel.eventHandler(event)
                        }
                    }
                },
                onDetailsEvent = { event ->
                    when (event) {
                        is SpaceDetailsEvents.OnItemClicked -> {
                            if (event.isLongClicked || viewModel.state.folders.any { it.isSelected } ||
                                viewModel.state.files.any { it.isSelected }
                            ) {
                                viewModel.onDetailedEvent(event.copy(isLongClicked = true))
                            } else {
                                if (event.folderId != null) {
                                    viewModel.onDetailedEvent(
                                        SpaceDetailsEvents.OnInit(
                                            parentId = event.folderId,
                                            isUpdate = true
                                        )
                                    )
                                } else if (event.fileId != null) {
                                    navHostController.navigate(
                                        CardDetails(
                                            fileId = event.fileId,
                                        )
                                    )
                                }
                            }
                        }

                        else -> {
                            viewModel.onDetailedEvent(event)
                        }
                    }
                }
            )
        }

        composable<SpaceDetails> { param ->
            val parentId = param.toRoute<SpaceDetails>().parentId
            val viewModel = hiltViewModel<SpaceDetailsVM>()

            if (refreshFlow.value == RefreshEventItem.REFRESH_FOLDER) {
                viewModel.eventHandler(SpaceDetailsEvents.OnRefresh)
                scope.launch {
                    RefreshEventHandler.eventFlow.emit(null)
                }
            }

            MySpaceDetailsScreen(
                parentId = parentId,
                state = viewModel.state,
                onEvent = { event ->
                    when (event) {
                        SpaceDetailsEvents.OnBackPressed -> {
                            scope.launch {
                                RefreshEventHandler.eventFlow.emit(RefreshEventItem.REFRESH_SPACE)
                            }
                            navHostController.navigateUp()
                        }

                        is SpaceDetailsEvents.OnCreateClicked -> {
                            val validationMsg = if (event.isFolderCreation)
                                viewModel.folderNameValidation()
                            else viewModel.fileValidation()

                            if (validationMsg.isNotEmpty()) {
                                showToast(validationMsg)
                            } else {
                                viewModel.eventHandler(event)
                            }
                        }

                        is SpaceDetailsEvents.OnItemClicked -> {
                            if (event.isLongClicked || viewModel.state.folders.any { it.isSelected } ||
                                viewModel.state.files.any { it.isSelected }
                            ) {
                                viewModel.eventHandler(event.copy(isLongClicked = true))
                            } else {
                                if (event.folderId != null) {
                                    viewModel.eventHandler(
                                        SpaceDetailsEvents.OnInit(
                                            parentId = event.folderId,
                                            isUpdate = true
                                        )
                                    )
                                } else if (event.fileId != null) {
                                    navHostController.navigate(
                                        CardDetails(
                                            fileId = event.fileId,
                                        )
                                    )
                                }
                            }
                        }

                        else -> {
                            viewModel.eventHandler(event)
                        }
                    }
                }
            )
        }

        composable<CardDetails> { param ->
            val fileId = param.toRoute<CardDetails>().fileId
            val viewModel = hiltViewModel<CardDetailsVM>()
            val context = LocalContext.current
            val shareContent = remember {
                ShareScreenShot(context)
            }
            CardDetailsScreen(
                fileId = fileId,
                state = viewModel.state,
                onEvent = { event ->
                    when (event) {
                        CardDetailsEvents.OnBackPressed -> {
                            scope.launch {
                                RefreshEventHandler.eventFlow.emit(RefreshEventItem.REFRESH_FOLDER)
                            }
                            navHostController.navigateUp()
                        }

                        is CardDetailsEvents.ShowMsg -> {
                            showToast(event.msg)
                        }

                        is CardDetailsEvents.OnUpdateClicked -> {
                            val validationMsg = viewModel.fileValidation()
                            if (validationMsg.isNotEmpty()) {
                                showToast(validationMsg)
                            } else {
                                viewModel.eventHandler(event)
                            }
                        }

                        is CardDetailsEvents.ShareImage -> {
                            shareContent.share(event.bitmap)
                        }

                        else -> {
                            viewModel.eventHandler(event)
                        }
                    }
                }
            )
        }
    }
}