package com.sanjay.myspace.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sanjay.myspace.R

@Composable
fun FolderCreationView(
    modifier: Modifier,
    folderName: String,
    onFolderNameChanged: (String) -> Unit,
    onCreate: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldWithLabel(
            label = stringResource(R.string.folderName),
            value = folderName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            onValueChange = {
                onFolderNameChanged(it)
            },
        )

        ThemeButton(
            modifier = Modifier
                .fillMaxWidth(0.5f),
            text = stringResource(R.string.create),
            onClick = {
                onCreate()
            }
        )
    }
}

@Composable
fun FileCreationView(
    modifier: Modifier,
    fileName: String,
    fileTitle: String,
    fileDes: String,
    onFileNameChanged: (String) -> Unit,
    onFileTitleChanged: (String) -> Unit,
    onFileDesChanged: (String) -> Unit,
    onCreate: () -> Unit,
    buttonText: String = stringResource(R.string.create),
) {
    Column(
        modifier = modifier
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldWithLabel(
            label = stringResource(R.string.file_name),
            value = fileName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            onValueChange = {
                onFileNameChanged(it)
            },
        )

        TextFieldWithLabel(
            label = stringResource(R.string.title),
            value = fileTitle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            onValueChange = {
                onFileTitleChanged(it)
            },
        )

        TextFieldWithLabel(
            label = stringResource(R.string.description),
            value = fileDes,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            keyboardType = KeyboardType.Text,
            minLines = 3,
            maxLines = 4,
            imeAction = ImeAction.Done,
            onValueChange = {
                onFileDesChanged(it)
            },
        )

        ThemeButton(
            modifier = Modifier
                .fillMaxWidth(0.5f),
            text = buttonText,
            onClick = {
                onCreate()
            }
        )
    }
}