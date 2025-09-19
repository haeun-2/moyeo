package com.mo.moyeo.common.util.lock;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class LockManagerTest {

    @Autowired
    LockedService lockedService;

    @Test
    public void lockTest1() {
        lockedService.lockedMethod1();
    }

    @Test
    public void lockTest2() {
        lockedService.lockedMethod2("lock1", "lock2");
    }

    @Test
    public void boxLockTest1() {
        lockedService.boxLockedMethod1();
    }

    @Test
    public void boxLockTest2() {
        lockedService.boxLockedMethod2(1L, CurrencyType.KRW);
    }

    @Test
    public void lockWithCallbackTest1() {
        lockedService.lockedMethodWithCallback1();
    }

    @Test
    public void lockWithCallbackTest2() {
        lockedService.lockedMethodWithCallback2();
    }

}