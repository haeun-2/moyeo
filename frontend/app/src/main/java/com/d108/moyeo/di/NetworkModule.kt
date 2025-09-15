package com.d108.moyeo.di

import com.d108.moyeo.data.remote.api.BankService
import com.d108.moyeo.data.remote.api.SignUpService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @Provides  // Hilt에게 "OkHttpClient 타입의 객체가 필요하면, 이 함수를 실행해서 만들어" 라고 알려주는 어노테이션
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // 서버와 통신하는 과정을 로그로 보기 쉽게 해주는 Interceptor
        val logger = HttpLoggingInterceptor().apply {  // 서버와 통신하는 모든 내용을 로그캣에 자세히 보여줌.
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logger)
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
}