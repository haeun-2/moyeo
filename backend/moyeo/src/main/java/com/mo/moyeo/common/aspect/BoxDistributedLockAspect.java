package com.mo.moyeo.common.aspect;

import com.mo.moyeo.common.annotation.BoxDistributedLock;
import com.mo.moyeo.common.annotation.BoxLockParam;
import com.mo.moyeo.common.util.CustomSpringELParser;
import com.mo.moyeo.common.util.lock.LockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BoxDistributedLockAspect {

    private final LockManager lockManager;

    private static final String BOX_LOCK_PREFIX = "box:";

    @Around("@annotation(com.mo.moyeo.common.annotation.BoxDistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        BoxDistributedLock boxDistributedLock = method.getAnnotation(BoxDistributedLock.class);

        // 1. BoxLockParam 배열 체크
        BoxLockParam[] lockParams = boxDistributedLock.value();
        if (lockParams.length == 0) {
            throw new IllegalArgumentException("@BoxDistributedLock lockParams 값이 비어있습니다. " + method.getName());
        }

        // 2. boxId, currencyCode 배열 생성
        String[] boxIds = new String[lockParams.length];
        String[] currencyCodes = new String[lockParams.length];

        for (int i = 0; i < lockParams.length; i++) {
            String boxIdKey = lockParams[i].boxId();
            boxIds[i] = boxIdKey.startsWith("#")
                    ? CustomSpringELParser.getParsedKey(signature.getParameterNames(), joinPoint.getArgs(), boxIdKey)
                    : boxIdKey;

            String currencyKey = lockParams[i].currencyCode();
            currencyCodes[i] = currencyKey.startsWith("#")
                    ? CustomSpringELParser.getParsedKey(signature.getParameterNames(), joinPoint.getArgs(), currencyKey)
                    : currencyKey;
        }

        // 3. 락 key 배열 생성
        String[] keys = new String[boxIds.length];
        for (int i = 0; i < boxIds.length; i++) {
            keys[i] = BOX_LOCK_PREFIX + boxIds[i] + ":" + currencyCodes[i];
        }

        // 3. LockManager로 락 수행
        return lockManager.executeWithLock(keys, boxDistributedLock.waitTime(), boxDistributedLock.leaseTime(), boxDistributedLock.timeUnit(), joinPoint::proceed);
    }

}
