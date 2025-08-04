package com.sanjay.myspace.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanjay.myspace.R
import com.sanjay.myspace.model.SpaceFile
import com.sanjay.myspace.ui.component.CustomText
import com.sanjay.myspace.ui.component.FileCreationView
import com.sanjay.myspace.ui.component.ProgressDialog
import com.sanjay.myspace.ui.component.ReadOnlyTextField
import com.sanjay.myspace.ui.component.SelectionWOClear
import com.sanjay.myspace.ui.component.Text14
import com.sanjay.myspace.ui.component.Text18
import com.sanjay.myspace.ui.component.TopBarComp
import com.sanjay.myspace.ui.event.CardDetailsEvents
import com.sanjay.myspace.ui.state.CardDetailsState
import com.sanjay.myspace.ui.state.ShareOptions
import com.sanjay.myspace.ui.theme.HintTextGrey
import com.sanjay.myspace.ui.theme.PrimaryClr
import com.sanjay.myspace.ui.theme.SecondaryClr
import com.sanjay.myspace.ui.theme.TextDark
import com.sanjay.myspace.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsScreen(
    fileId: Int?,
    state: CardDetailsState,
    onEvent: (CardDetailsEvents) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    var share by remember { mutableStateOf(false) }

    if (state.isInit) {
        onEvent(CardDetailsEvents.OnInit(fileId))
        ProgressDialog()
    }

    if (state.showShareOption) {
        SelectionWOClear(
            dialogLabel = "Option",
            items = state.shareOptions,
            onDismiss = {
                onEvent(CardDetailsEvents.HandleShareOption(false))
            },
            onClick = {
                onEvent(CardDetailsEvents.HandleShareOption(false))
                if (it != null) {
                    when (it.value) {
                        ShareOptions.ONLY_CONTENT.name -> share = true
                        ShareOptions.ENTIRE_SCREEN.name -> {
                            coroutineScope.launch {
                                onEvent(
                                    CardDetailsEvents.ShareImage(
                                        graphicsLayer.toImageBitmap().asAndroidBitmap()
                                    )
                                )
                            }
                        }
                    }
                } else {
                    onEvent(CardDetailsEvents.ShowMsg("Failed to share"))
                }
            }
        )
    }

    if (state.showFileUpdateView) {
        ModalBottomSheet(
            containerColor = White,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            onDismissRequest = {
                onEvent(CardDetailsEvents.HandleFileUpdateView(false))
            }
        ) {
            FileCreationView(
                modifier = Modifier
                    .fillMaxWidth(),
                fileName = state.fileName,
                fileTitle = state.fileTitle,
                fileDes = state.fileDes,
                onFileNameChanged = {
                    onEvent(CardDetailsEvents.OnFileNameChanged(it))
                },
                onFileTitleChanged = {
                    onEvent(CardDetailsEvents.OnFileTitleChanged(it))
                },
                onFileDesChanged = {
                    onEvent(CardDetailsEvents.OnFileDesChanged(it))
                },
                onCreate = {
                    onEvent(CardDetailsEvents.OnUpdateClicked)
                },
                buttonText = stringResource(R.string.update)
            )
        }
    }

    BackHandler {
        onEvent(CardDetailsEvents.OnBackPressed)
    }

    if (share) {
        ShareContentUI(
            fileData = state.fileData
        ) { bitmap ->
            share = false
            onEvent(CardDetailsEvents.ShareImage(bitmap))
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            },
        containerColor = White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopBarComp(
                title = state.fileData?.fileName ?: "",
                onBackClick = {
                    onEvent(CardDetailsEvents.OnBackPressed)
                },
                actions = {
                    IconButton(
                        onClick = {
                            onEvent(CardDetailsEvents.OnFavoriteClick)
                        }
                    ) {
                        Icon(
                            imageVector = if (state.fileData?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (state.fileData?.isFavorite == true) PrimaryClr else TextDark
                        )
                    }

                    TextButton(
                        onClick = {
                            onEvent(CardDetailsEvents.HandleFileUpdateView(true))
                        }
                    ) {
                        Text14(
                            text = "Edit",
                            modifier = Modifier
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(CardDetailsEvents.HandleShareOption(true))
                },
                containerColor = PrimaryClr,
                contentColor = White,
                shape = FloatingActionButtonDefaults.largeShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = stringResource(R.string.share)
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 15.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                ReadOnlyTextField(
                    label = stringResource(R.string.title),
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = state.fileData?.title ?: ""
                )

                ReadOnlyTextField(
                    label = stringResource(R.string.description),
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = state.fileData?.description ?: ""
                )

                ReadOnlyTextField(
                    label = stringResource(R.string.created_at),
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = state.fileData?.createdAt ?: ""
                )

                ReadOnlyTextField(
                    label = stringResource(R.string.updated_at),
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = state.fileData?.updatedAt ?: ""
                )
            }
        }
    }
}

@Composable
private fun ShareContentUI(
    fileData: SpaceFile?,
    bitmap: (Bitmap) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            }
            .background(White),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = Color.Gray
                    .copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text18(
                        text = fileData?.fileName ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Icon(
                        imageVector = if (fileData?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (fileData?.isFavorite == true) PrimaryClr else TextDark
                    )
                }
                ShareItemUI(
                    title = stringResource(R.string.title),
                    value = fileData?.title
                )
                ShareItemUI(
                    title = stringResource(R.string.description),
                    value = fileData?.description
                )
                ShareItemUI(
                    title = stringResource(R.string.created_at),
                    value = fileData?.createdAt
                )
                ShareItemUI(
                    title = stringResource(R.string.updated_at),
                    value = fileData?.updatedAt
                )
            }
        }
    }
    SideEffect {
        coroutineScope.launch {
            bitmap(graphicsLayer.toImageBitmap().asAndroidBitmap())
        }
    }
}

@Composable
private fun ShareItemUI(
    title: String,
    value: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        CustomText(
            text = title,
            modifier = Modifier,
            textColor = HintTextGrey,
            fontSize = 10.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .border(
                    width = 0.7.dp,
                    color = SecondaryClr,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text14(
                text = value ?: "NA",
                modifier = Modifier
            )
        }
    }
}