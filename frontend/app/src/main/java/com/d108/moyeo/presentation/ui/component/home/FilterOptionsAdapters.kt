package com.d108.moyeo.presentation.ui.component.home

/**
 * - `sealed interface FilterOptions` 로 상위 타입을 고정하고, 화면별로 필요한 어댑터(래퍼) 타입만 선언.
 * - 각 화면(Wallet, Box)은 "자기 화면 전용 데이터 클래스"를 유지하고, 바텀시트 열고/닫을 때 아래 어댑터 타입으로 변환하여 주고받음.
 *
 * 사용 흐름
 *  - [화면 ]    WalletFilterOptions  <—변환—>  WalletFilterOptionsAdp(여기)
 *  - [바텀시트 ] CommonFilterBottomSheet(FilterOptions = 어댑터)
 *  - [확인 시]   updated: FilterOptions(= 어댑터) —변환→ WalletFilterOptions (화면 상태 반영)
 *
 * 참고
 *  - Wallet/Box 쪽의 실제 화면 전용 데이터 클래스와 확장함수는 각 화면 패키지에.
 *    ex) Wallet:
 *      fun WalletFilterOptions.toAdapter(): WalletFilterOptionsAdp = ...
 *      fun FilterOptions.toWallet(): WalletFilterOptions = when(this) { is WalletFilterOptionsAdp -> ... }
 *
 *  - CommonFilterBottomSheet는 FilterOptions만 받아 동작.
 *    (호출부에서 toAdapter() / toWallet() 또는 toBox() 로 상호 변환)
 */

/* ********************************************************************************************** *
 * 1) sealed 상위 타입
 * ********************************************************************************************** */

/**
 * 공통 필터 상위 타입.
 *
 * @property period 기간(예: "1개월", "3개월", "6개월", "직접 설정")
 * @property scope  범위/카테고리(예: "전체", "입금", "출금", "환전", ...)
 * @property sort   정렬 방식(예: "최신", "과거")
 *
 * @see WalletFilterOptionsAdp
 * @see BoxFilterOptionsAdp
 */

sealed interface FilterOptions {
    val period: String
    val scope: String
    val sort: String

    /**
     * 동일한 서브타입으로 값만 바꿔 새 인스턴스를 반환합니다.
     *
     * - CommonFilterBottomSheet에서 "확인" 버튼을 눌렀을 때 호출됩니다.
     * - 구현체에서는 data class의 copy(...)로 구현하여 "동일 타입 유지"를 보장합니다.
     *
     * @param period 변경할 기간 값
     * @param scope  변경할 범위/카테고리 값
     * @param sort   변경할 정렬 값
     * @return 변경된 값을 가진 **같은 서브타입**의 FilterOptions
     */

    fun rebuild(period: String, scope: String, sort: String): FilterOptions
}

/* ********************************************************************************************** *
 * 2) Wallet용 어댑터(래퍼)
 *    - Wallet 화면 전용 데이터 클래스를 바텀시트와 주고받기 위한 중간 타입입니다.
 *    - CommonFilterBottomSheet는 이 어댑터를 FilterOptions로 다룹니다.
 * ********************************************************************************************** */

/**
 * Wallet 화면 전용 어댑터.
 *
 * - Wallet 화면의 "내부 전용 모델(예: WalletFilterOptions)"과 1:1로 매핑되는 래퍼입니다.
 * - 화면 <-> 바텀시트 간 변환 확장함수는 Wallet 패키지 쪽에 둡니다.
 *   ex) WalletFilterOptions.toAdapter(): WalletFilterOptionsAdp
 *       FilterOptions.toWallet(): WalletFilterOptions (when(this) { is WalletFilterOptionsAdp -> ... })
 */

data class WalletFilterOptionsAdp(
    override val period: String,
    override val scope: String,
    override val sort: String
) : FilterOptions {

    /** data class 의 copy(...)를 이용해 동일 타입으로 재생성합니다. */
    override fun rebuild(period: String, scope: String, sort: String): FilterOptions =
        copy(period = period, scope = scope, sort = sort)
}

/* ********************************************************************************************** *
 * 3) Box용 어댑터(래퍼)
 *    - Box 화면 전용 데이터 클래스를 바텀시트와 주고받기 위한 중간 타입입니다.
 * ********************************************************************************************** */

/**
 * Box 화면 전용 어댑터.
 *
 * - Box 화면의 "내부 전용 모델(예: BoxFilterOptions)"과 1:1로 매핑되는 래퍼입니다.
 * - 화면 <-> 바텀시트 간 변환 확장함수는 Box 패키지 쪽에 둡니다.
 *   ex) BoxFilterOptions.toAdapter(): BoxFilterOptionsAdp
 *       FilterOptions.toBox(): BoxFilterOptions (when(this) { is BoxFilterOptionsAdp -> ... })
 */

data class BoxFilterOptionsAdp(
    override val period: String,
    override val scope: String,
    override val sort: String
) : FilterOptions {

    /** data class 의 copy(...)를 이용해 동일 타입으로 재생성합니다. */
    override fun rebuild(period: String, scope: String, sort: String): FilterOptions =
        copy(period = period, scope = scope, sort = sort)
}