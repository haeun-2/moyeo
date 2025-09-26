package com.d108.moyeo.data.mapper

import androidx.compose.ui.graphics.Color
import com.d108.moyeo.core.BoxStoreUiState
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.data.remote.dto.box.BalanceDto
import com.d108.moyeo.data.remote.dto.box.BoxDetailResponseDto
import com.d108.moyeo.data.remote.dto.box.BoxMemberDto
import com.d108.moyeo.data.remote.dto.box.GroupBoxResponseDto
import com.d108.moyeo.data.remote.dto.box.PermissionDto
import com.d108.moyeo.data.remote.dto.box.PersonalBoxResponseDto
import com.d108.moyeo.domain.model.box.Balance
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.box.BoxDetail
import com.d108.moyeo.domain.model.box.BoxMember
import com.d108.moyeo.domain.model.box.BoxType
import com.d108.moyeo.domain.model.box.Permission
import com.d108.moyeo.presentation.theme.boxAvailableColors
import com.d108.moyeo.util.textColorUtil
import kotlinx.coroutines.flow.first
import java.text.DecimalFormat
import kotlin.math.abs

fun PersonalBoxResponseDto.toDomain() = Box(
    id = boxId,
    name = name,
    balances = balances.map(BalanceDto::toDomain),
    type = BoxType.from(type),
    isBookmarked = false  // 개인 박스는 언제나 false  // TODO: 트루로 변경
)

fun GroupBoxResponseDto.toDomain() = Box(
    id = boxId,
    name = name,
    balances = balances.map(BalanceDto::toDomain),
    type = BoxType.from(type),
    isBookmarked = isBookmarked // 모여 박스는 서버 값을 받음
)

fun BalanceDto.toDomain() = Balance(
    currency = currency,
    balance = balance
)

fun BoxDetailResponseDto.toDomain(): BoxDetail {
    // DTO를 Box, Permission, List<BoxMember> 세 부분으로 분해하여 조립합니다.
    return BoxDetail(
        box = Box(
            id = this.boxId,
            name = this.name,
            balances = this.balances.map { it.toDomain() },
            type = BoxType.from(this.type)
        ),
        myPermission = this.permission.toDomain(),
        members = this.members.map { it.toDomain() }
    )
}

fun PermissionDto.toDomain(): Permission {
    return Permission(
        canTransfer = this.canTransfer,
        canPayment = this.canPayment,
        canExchange = this.canExchange,
        isOwner = this.isOwner
    )
}

fun BoxMemberDto.toDomain(): BoxMember {
    val permission = Permission(
        canTransfer = this.canTransfer,
        canPayment = this.canPayment,
        canExchange = this.canExchange,
        isOwner = this.isOwner
    )
    return BoxMember(
        id = this.boxMemberId,
        name = this.name,
        permission = permission
    )
}
suspend fun Box.toBoxStoreUiState(userDataManager: UserDataManager): BoxStoreUiState {
    val localColor = if (this.type == BoxType.PERSONAL) {
        userDataManager.walletColorFlow.first()
    } else {
        userDataManager.getGroupColor(this.id)
    }
    val finalColor = localColor?.let { Color(it) } ?: colorFromId(this.id)

    val serverIsBookmarked = this.isBookmarked
    val localIsBookmarked = userDataManager.isBookmarked(this.id)
    val finalIsBookmarked = serverIsBookmarked || localIsBookmarked

    val repr = this.balances.maxByOrNull { it.balance }
    val amountText = if (repr == null) "잔액 없음" else formatAmount(repr.currency, repr.balance)

    return BoxStoreUiState(
        id = this.id,
        title = this.name,
        bg = finalColor,
        textColor = textColorUtil(finalColor),
        isBookmarked = finalIsBookmarked,
        amount = amountText,
        balances = this.balances,
        type = this.type
    )
}

private fun formatAmount(code: String, amount: Double): String {
    val formatter = DecimalFormat("#,###.##")
    val formattedNumber = formatter.format(amount)
    return "$formattedNumber $code"
}

private fun colorFromId(id: Long): Color {
    val base = abs(id.hashCode())
    return boxAvailableColors[base % boxAvailableColors.size]
}