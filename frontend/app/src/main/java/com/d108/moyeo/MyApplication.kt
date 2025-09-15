package com.d108.moyeo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // 이 클래스가 우리 앱의 시작점이니, 앱 전체에서 사용할 의존성 컨테이너를 여기서 만들도록 지시
class MyApplication: Application() {  // 어플리케이션 실행 시 최초 1회만 실행

}