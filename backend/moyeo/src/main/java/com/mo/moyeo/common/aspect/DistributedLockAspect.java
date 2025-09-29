package com.mo.moyeo.common.aspect;

import com.mo.moyeo.common.annotation.DistributedLock;
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
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

    private final LockManager lockManager;

    @Around("@annotation(com.mo.moyeo.common.annotation.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // 1. key 배열 체크
        String[] keys = distributedLock.keys();
        if (keys.length == 0) {
            throw new IllegalArgumentException("@DistributedLock keys 값이 비어있습니다. " + method.getName());
        }

        // 2. SpEL 파싱 후 락 키 생성
        String[] lockKeys = Arrays.stream(keys)
                .map(k -> k.startsWith("#") ? CustomSpringELParser.getParsedKey(signature.getParameterNames(), joinPoint.getArgs(), k) : k)
                .toArray(String[]::new);

        // 3. LockManager로 락 수행
        return lockManager.executeWithLock(lockKeys, distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit(), joinPoint::proceed);
    }

}
