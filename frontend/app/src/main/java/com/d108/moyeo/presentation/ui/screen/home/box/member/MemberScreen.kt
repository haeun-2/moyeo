package com.d108.moyeo.presentation.ui.screen.home.box.member

import android.R.attr.onClick
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.R
import com.d108.moyeo.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberScreen(
    navController: NavController,
    viewModel: MemberViewModel = hiltViewModel()
) {

    val ui by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is MemberNavEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    BackHandler { viewModel.onBackClick() }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Medium)
            ) {
                IconButton(
                    onClick = { viewModel.onBackClick() },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "뒤로가기"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = Spacing.Medium,
                    end = Spacing.Medium
                )
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    ui.isLoading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    ui.error != null -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text(text = ui.error ?: "오류") }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ui.members, key = { it.member.id }) { uiModel ->
                            MemberCard(
                                memberUi = uiModel,
                                isOwnerView = ui.isCurrentUserOwner,
                                onPermissionChange = viewModel::onPermissionChange
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = Spacing.Medium)
            ) {
                Text("확인")
            }
        }
    }
}

@Composable
private fun MemberCard(
    memberUi: BoxMemberUi,
    isOwnerView: Boolean,
    onPermissionChange: (memberId: Long, type: PermissionType) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF0F0F0),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = memberUi.member.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (memberUi.member.permission.isOwner) {
                    Icon(
                        painter = painterResource(R.drawable.crown_24), // 왕관 아이콘
                        contentDescription = "방장",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp)
                    )
                }
            }

            if (memberUi.expanded) {
                Row (
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                ) {
                    CapabilityChip(
                        labelWhenTrue = "정산 가능",
                        labelWhenFalse = "정산 불가",
                        enabled = memberUi.member.permission.canTransfer,
                        isClickable = isOwnerView,
                        onClick = { onPermissionChange(memberUi.member.id, PermissionType.TRANSFER) }

                    )
                    Spacer(modifier = Modifier.weight(1f))
                    CapabilityChip(
                        labelWhenTrue = "결제 가능",
                        labelWhenFalse = "결제 불가",
                        enabled = memberUi.member.permission.canPayment,
                        isClickable = isOwnerView,
                        onClick = { onPermissionChange(memberUi.member.id, PermissionType.PAYMENT) }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    CapabilityChip(
                        labelWhenTrue = "환전 가능",
                        labelWhenFalse = "환전 불가",
                        enabled = memberUi.member.permission.canExchange,
                        isClickable = isOwnerView,
                        onClick = { onPermissionChange(memberUi.member.id, PermissionType.EXCHANGE) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CapabilityChip(
    labelWhenTrue: String,
    labelWhenFalse: String,
    enabled: Boolean,
    isClickable: Boolean,
    onClick: () -> Unit
) {
    val purple = Color(0xFF9864FF)
    val bg = if (enabled) purple else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clickable(enabled = isClickable, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (enabled) labelWhenTrue else labelWhenFalse,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = fg,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
