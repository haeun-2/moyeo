package com.d108.moyeo.presentation.ui.screen.home.box.member

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.d108.moyeo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberScreen(
    boxId: Long,
    onBack: () -> Unit,
    viewModel: MemberViewModel = hiltViewModel()
) {
    val ui by viewModel.uiState.collectAsState()

    LaunchedEffect(boxId) { viewModel.load(boxId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("회원 목록") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { inner ->
        when {
            ui.isLoading -> Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            ui.error != null -> Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(text = ui.error ?: "오류") }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ui.members, key = { it.id }) { member ->
                    MemberCard(
                        member = member,
                        onToggle = { viewModel.toggleExpand(member.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MemberCard(
    member: BoxMemberUi,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggle() }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (member.isOwner) {
                    Icon(
                        painter = painterResource(R.drawable.crown_24), // 왕관 아이콘
                        contentDescription = "방장",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp)
                    )
                }

                Icon(
                    imageVector = if (member.expanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (member.expanded) "닫기" else "열기"
                )
            }

            if (member.expanded) {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 디자인 시안의 섹션 타이틀(불필요하면 제거)
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CapabilityChip(
                            labelWhenTrue = "정산 가능",
                            labelWhenFalse = "정산 불가",
                            enabled = member.capabilities.settleAllowed
                        )
                        CapabilityChip(
                            labelWhenTrue = "결제 가능",
                            labelWhenFalse = "결제 불가",
                            enabled = member.capabilities.payAllowed
                        )
                        CapabilityChip(
                            labelWhenTrue = "환전 가능",
                            labelWhenFalse = "환전 불가",
                            enabled = member.capabilities.fxAllowed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CapabilityChip(
    labelWhenTrue: String,
    labelWhenFalse: String,
    enabled: Boolean
) {
    val purple = Color(0xFF9864FF)
    val bg = if (enabled) purple else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (enabled) labelWhenTrue else labelWhenFalse,
            style = MaterialTheme.typography.labelLarge.copy(
                color = fg,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
