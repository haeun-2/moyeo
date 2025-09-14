package com.mo.moyeo.common.aspect;

import com.mo.moyeo.common.annotation.BoxDistributedLock;
import com.mo.moyeo.common.annotation.BoxLockParam;
import com.mo.moyeo.common.annotation.DistributedLock;
import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BoxDistributedLockAspect {

    private static final String BOX_LOCK_PREFIX = "box:";

    private final DistributedLockAspect distributedLockAspect;

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

        // 2. BoxLockParam을 기반으로 락 키 배열 생성
        String[] lockKeys = Arrays.stream(lockParams)
                .map(param -> {
                    // SpEL 파싱으로 boxId와 currency 값 추출
                    String boxId = CustomSpringELParser.getParsedKey(signature.getParameterNames(), joinPoint.getArgs(), param.boxId());
                    String currencyCode = CustomSpringELParser.getParsedKey(signature.getParameterNames(), joinPoint.getArgs(), param.currencyCode());

                    // 락 키 생성: "box:{boxId}:{currencyCode}"
                    return BOX_LOCK_PREFIX + boxId + ":" + currencyCode;
                })
                .toArray(String[]::new);

        // 3. 공통 락 로직 실행
        return distributedLockAspect.executeLockWithKeys(joinPoint, lockKeys, boxDistributedLock.waitTime(), boxDistributedLock.leaseTime(), boxDistributedLock.timeUnit());
    }

}
