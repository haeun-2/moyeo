package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.NotificationContentDto
import com.d108.moyeo.domain.model.Notification

private fun NotificationContentDto.parseBody(): ParsedBody {
    val lines = body
        .trim()
        .split('\n')
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    val time = lines.getOrNull(0)

    // 후보군
    val rest = lines.drop(1).toMutableList()
    var sender: String? = null
    var amount: String? = null
    var balance: String? = null

    // balance: "잔액 ..." 형태
    val balIdx = rest.indexOfFirst { it.startsWith("잔액") }
    if (balIdx >= 0) {
        balance = rest[balIdx]
        rest.removeAt(balIdx)
    }

    // amount: 환전은 "→" 포함 / 일반 이체는 "숫자 + 통화" 패턴
    val arrowIdx = rest.indexOfFirst { it.contains("→") }
    if (arrowIdx >= 0) {
        amount = rest[arrowIdx]
        rest.removeAt(arrowIdx)
    } else {
        // 단순 금액/통화 라인 후보 (KRW, JPY, USD 등 통화코드 포함)
        val moneyIdx = rest.indexOfFirst { Regex("""\d|\b[A-Z]{3}\b""").containsMatchIn(it) }
        if (moneyIdx >= 0) {
            amount = rest[moneyIdx]
            rest.removeAt(moneyIdx)
        }
    }

    // 남은 한 줄이 있으면 sender로 간주 (이체 케이스)
    if (rest.isNotEmpty()) sender = rest.first()

    return ParsedBody(time, sender, amount, balance)
}

private data class ParsedBody(
    val time: String?,
    val sender: String?,
    val amount: String?,
    val balance: String?
)

fun NotificationContentDto.toDomain(): Notification {
    val parsed = parseBody()
    return Notification(
        transactionId = transactionId,
        title = title,
        boxId = data?.get("box")?.toLongOrNull(),
        receivedAt = receivedAt.replace('T', ' ').take(16), // "YYYY-MM-DD HH:mm"
        time = parsed.time,
        sender = parsed.sender,
        amount = parsed.amount,
        balance = parsed.balance
    )
}
