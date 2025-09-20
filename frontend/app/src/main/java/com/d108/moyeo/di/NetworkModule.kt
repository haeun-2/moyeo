package com.d108.moyeo.di

import android.content.Context
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.data.remote.api.AuthService
import com.d108.moyeo.data.remote.api.BankService
import com.d108.moyeo.data.remote.api.BankingService
import com.d108.moyeo.data.remote.api.BoxHistoryService
import com.d108.moyeo.data.remote.api.BoxService
import com.d108.moyeo.data.remote.api.BoxStatisticsService
import com.d108.moyeo.data.remote.api.ExchangeService // 새 import 추가
import com.d108.moyeo.data.remote.api.NotificationService
import com.d108.moyeo.data.remote.api.PaymentService
import com.d108.moyeo.data.remote.api.SignUpService
import com.d108.moyeo.data.remote.interceptor.AuthInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module  // 이 객체가 Hilt에게 의존성(dependency)을 제공하는 방법을 알려주는 '설명서'
@InstallIn(SingletonComponent::class)  // 앱이 살아있는 동안 OkHttpClient와 Retrofit 객체는 딱 한 번만 만들어지고 계속 재사용
object NetworkModule {  // 여러 부품을 조립해서 새로운 것을 '만들어내야' 하는 복잡한 객체들을 책임 -> object

    private const val BASE_URL = "http://j13d108.p.ssafy.io:8080/"

    //
    @Provides
    @Singleton
    fun provideUserDataManager(@ApplicationContext context: Context): UserDataManager {
        return UserDataManager(context)
    }

    @Provides  // Hilt에게 "OkHttpClient 타입의 객체가 필요하면, 이 함수를 실행해서 만들어" 라고 알려주는 어노테이션
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor // 인터셉터 등록
    ): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(authInterceptor) // 모든 요청이 이 인터셉터를 통과하도록 등록
            .build()
    }

    @Provides  // Hilt에게 "Retrofit 타입의 객체가 필요하면, 이 함수를 실행해서 만들어" 라고 알려주는 어노테이션
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }

    @Provides  // Hilt에게 SignUpService 타입의 객체가 필요하면
    @Singleton
    fun provideSignUpService(retrofit: Retrofit): SignUpService {
        return retrofit.create(SignUpService::class.java)  // 이걸 실행해서 만들어라
    }

    /*
    * 은행과 관련둰 서비스
     */
    @Provides
    @Singleton
    fun provideBankService(retrofit: Retrofit): BankService {
        return retrofit.create(BankService::class.java)
    }


    /*
    * 로그인과 관련된 서비스
     */
    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideBoxService(retrofit: Retrofit): BoxService =
        retrofit.create(BoxService::class.java)


    /*
    결제와 관련된 서비스
     */
    @Provides
    @Singleton
    fun providePaymentService(retrofit: Retrofit): PaymentService {
        return retrofit.create(PaymentService::class.java)
    }

    @Provides
    @Singleton
    fun provideBankingService(retrofit: Retrofit): BankingService =
        retrofit.create(BankingService::class.java)


    /*
    히스토리와 관련된 서비스
     */
    @Provides
    @Singleton
    fun provideBoxStatisticsService(retrofit: Retrofit): BoxStatisticsService {
        return retrofit.create(BoxStatisticsService::class.java)
    }

    @Provides
    @Singleton
    fun provideBoxHistoryService(retrofit: Retrofit): BoxHistoryService {
        return retrofit.create(BoxHistoryService::class.java)
    }

    /*
    * 환율과 관련된 서비스
     */
    @Provides
    @Singleton
    fun provideExchangeService(retrofit: Retrofit): ExchangeService { // 함수 이름 및 반환 타입 변경
        return retrofit.create(ExchangeService::class.java) // 생성시 ExchangeService 사용
    }

    /*
    * 알림 서비스
    */
    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }
}