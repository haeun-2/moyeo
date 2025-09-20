package com.d108.moyeo.di

import com.d108.moyeo.data.repository.AuthRepositoryImpl
import com.d108.moyeo.data.repository.BankRepositoryImpl
import com.d108.moyeo.data.repository.BoxHistoryRepositoryImpl
import com.d108.moyeo.data.repository.FidRepositoryImpl
import com.d108.moyeo.data.repository.BoxRepositoryImpl
import com.d108.moyeo.data.repository.BoxStatisticsRepositoryImpl
import com.d108.moyeo.data.repository.ExchangeRepositoryImpl
import com.d108.moyeo.data.repository.NotificationRepositoryImpl
import com.d108.moyeo.data.repository.PaymentRepositoryImpl
import com.d108.moyeo.data.repository.SignUpRepositoryImpl
import com.d108.moyeo.data.repository.TransferRepositoryImpl
import com.d108.moyeo.domain.repository.AuthRepository
import com.d108.moyeo.domain.repository.BankRepository
import com.d108.moyeo.domain.repository.BoxHistoryRepository
import com.d108.moyeo.domain.repository.BoxRepository
import com.d108.moyeo.domain.repository.BoxStatisticsRepository
import com.d108.moyeo.domain.repository.ExchangeRepository
import com.d108.moyeo.domain.repository.FidRepository
import com.d108.moyeo.domain.repository.NotificationRepository
import com.d108.moyeo.domain.repository.PaymentRepository
import com.d108.moyeo.domain.repository.SignUpRepository
import com.d108.moyeo.domain.repository.TransferRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
RepositoryModule은 우리가 직접 호출하는 파일이 아닙니다. 이 파일은 Hilt가 컴파일 시점에 미리 읽어보는 '규칙집'입니다.

Hilt는 이 규칙집을 보고, "아하! SignUpViewModel이 SignUpRepository를 달라고 하면, 내가 SignUpRepositoryImpl을 만들어서 주면 되는구나!" 라고 학습합니다.
그리고 앱이 실행될 때, SignUpViewModel을 만드는 그 순간에 Hilt가 알아서 SignUpRepositoryImpl의 인스턴스를 조립하여 생성자에 넣어주는 것입니다.

결론적으로, RepositoryModule은 ViewModel과 Repository 사이의 '느슨한 연결'을 만들어주는 매우 중요한 접착제입니다.
이를 통해 ViewModel은 구체적인 구현체(Impl)에 대해 전혀 알 필요가 없게 되어, 나중에 테스트하거나 다른 구현체로 쉽게 교체할 수 있는 유연하고 좋은 구조가 완성됩니다.
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {  // @Binds 함수는 함수 본문이 없어야만 합니다. 그래서 추상 클래스로 선언

    // @Binds 어노테이션은 Hilt에게 "누군가 SignUpRepository(설계도)를 달라고 하면,
    // SignUpRepositoryImpl(실제 일꾼)의 인스턴스를 만들어서 줘" 라고 알려주는 역할을 합니다.
    // 인터페이스와 구현체가 명확할 때 사용하는, 가장 효율적인 방식입니다.
    @Binds  // @Binds 함수는 함수 본문이 없어야만 합니다.
    @Singleton
    abstract fun bindSignUpRepository(
        signUpRepositoryImpl: SignUpRepositoryImpl  // 제공할 완제품
    ): SignUpRepository // 설계도

    @Binds
    @Singleton
    abstract fun bindBankRepository(
        bankRepositoryImpl: BankRepositoryImpl
    ): BankRepository
    // TODO: 나중에 AuthRepository, BoxRepository 등 다른 Repository들도 여기에 추가합니다.

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFidRepository(
        fidRepositoryImpl: FidRepositoryImpl
    ): FidRepository

    @Binds
    @Singleton
    abstract fun bindBoxRepository(
        boxRepositoryImpl: BoxRepositoryImpl
    ): BoxRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindExchangeRepository(
        exchangeRepositoryImpl: ExchangeRepositoryImpl
    ): ExchangeRepository

    @Binds
    @Singleton
    abstract fun bindTransferRepository(
        transferRepositoryImpl: TransferRepositoryImpl
    ): TransferRepository

    @Binds
    @Singleton
    abstract fun bindBoxStatisticsRepository(
        boxStatisticsRepositoryImpl: BoxStatisticsRepositoryImpl
    ): BoxStatisticsRepository

    @Binds
    @Singleton
    abstract fun bindBoxHistoryRepository(
        boxHistoryRepositoryImpl: BoxHistoryRepositoryImpl
    ): BoxHistoryRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

}