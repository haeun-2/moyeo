package com.mo.moyeo.common.aspect;

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
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "lock:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

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
                .map(k -> CustomSpringELParser.getParsedKey(signature.getParameterNames(), joinPoint.getArgs(), k))
                .toArray(String[]::new);

        // 3. 공통 락 로직 실행
        return executeLockWithKeys(joinPoint, lockKeys, distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
    }

    public Object executeLockWithKeys(ProceedingJoinPoint joinPoint, String[] lockKeys, long waitTime, long leaseTime, TimeUnit timeUnit) throws Throwable {
        // 1. 락 배열 생성
        RLock[] locks = Arrays.stream(lockKeys)
                .map(key -> redissonClient.getLock(REDISSON_LOCK_PREFIX + key))
                .toArray(RLock[]::new);

        // 2. 멀티락 생성
        RLock multiLock = new RedissonMultiLock(locks);

        // 로그 출력용
        String threadName = Thread.currentThread().getName();
        List<String> keyNames = Arrays.stream(locks).map(RLock::getName).toList();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();

        // 3. 락 획득 시도
        try {
            log.debug("락 획득 시도 - method: {}, keys: {}, thread: {}", methodName, keyNames, threadName);
            boolean available = multiLock.tryLock(waitTime, leaseTime, timeUnit);
            if (!available) {
                log.warn("락 획득 실패 - method: {}, keys: {}, thread: {}", methodName, keyNames, threadName);
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

            log.info("락 획득 성공 - method: {}, keys: {}, thread: {}", methodName, keyNames, threadName);
            // 4. 로직 실행
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            log.error("락 대기 중 인터럽트 발생 - method: {}, keys: {}, thread: {}", methodName, keyNames, threadName);
            throw new InterruptedException();
        } finally {
            // 5. 락 해제
            try {
                multiLock.unlock();
                log.info("락 해제 성공 - method: {}, keys: {}, thread: {}", methodName, keyNames, threadName);
            } catch (IllegalMonitorStateException e) {
                log.warn("락 이미 해재됨 - method: {}, keys: {}, thread: {}", methodName, keyNames, threadName);
            }
        }
    }


}
