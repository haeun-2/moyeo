package com.d108.moyeo.presentation.ui.screen.home.create

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CreateBoxScreen(
    onBackClick: () -> Unit,
    onFinishClick: () -> Unit,
    viewModel: CreateBoxViewModel = hiltViewModel()
) {
    val ui = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { ev ->
            when (ev) {
                is CreateBoxEvent.NavigateResult -> viewModel.moveToResult(ev.boxId)
                is CreateBoxEvent.ShowError      -> snackbarHostState.showSnackbar(ev.message)
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "back")
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                when (ui.currentStep) {
                    CreateBoxStep.NAME -> {
                        CreateBoxNameContent(
                            name = ui.name,
                            isLoading = ui.isLoading,
                            onNameChange = viewModel::onNameChange,
                            onConfirm = { viewModel.onConfirmClick() }
                        )
                    }

                    CreateBoxStep.RESULT -> {
                        CreateBoxResultContent(
                            inviteLink = ui.inviteLink,
                            expiresAt = ui.expiresAt,
                            onCopyOnly = {
                                ui.inviteLink?.let { link ->
                                    scope.launch {
                                        clipboard.setText(AnnotatedString(link))
                                        Toast.makeText(context, "링크를 복사했어요", Toast.LENGTH_SHORT).show()
                                    }
                                } ?: scope.launch {
                                    Toast.makeText(context, "초대 링크가 없어요", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCopyAndClose = {
                                scope.launch {
                                    ui.inviteLink?.let { link ->
                                        clipboard.setText(AnnotatedString(link))
                                        Toast.makeText(context, "링크를 복사했어요", Toast.LENGTH_SHORT).show()
                                    } ?: Toast.makeText(context, "초대 링크가 없어요", Toast.LENGTH_SHORT).show()
                                    onFinishClick()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
