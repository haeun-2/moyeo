package com.mo.moyeo.common.util.lock;

import com.mo.moyeo.common.annotation.BoxDistributedLock;
import com.mo.moyeo.common.annotation.BoxLockParam;
import com.mo.moyeo.common.annotation.DistributedLock;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class LockedService {

    @Autowired LockManager lockManager;

    @DistributedLock(keys = {"lock1", "lock2"})
    public void lockedMethod1() {
        System.out.println("lockMethod1");
    }

    @DistributedLock(keys = {"#param1", "#param2"})
    public void lockedMethod2(String param1, String param2) {
        System.out.println("lockMethod2");
    }

    @BoxDistributedLock({
            @BoxLockParam(boxId = "1", currencyCode = "KRW")
    })
    public void boxLockedMethod1() {
        System.out.println("boxLockedMethod1");
    }

    @BoxDistributedLock({
            @BoxLockParam(boxId = "#boxId", currencyCode = "#currencyType")
    })
    public void boxLockedMethod2(Long boxId, CurrencyType currencyType) {
        System.out.println("boxLockedMethod2");
    }

    public void lockedMethodWithCallback1() {
        String[] keys = {"lock1", "lock2"};
        lockManager.executeWithLock(
                keys,
                () -> {
                    System.out.println("lockedMethodWithCallback1");
                    return null;
                }
        );
    }

    public void lockedMethodWithCallback2() {
        String[] keys = {"lock1", "lock2"};
        String result = lockManager.executeWithLock(
                keys,
                () -> {
                    System.out.println("lockedMethodWithCallback2");
                    return "LOCKED RESULT";
                }
        );
        System.out.println("result = " + result);
    }

}
