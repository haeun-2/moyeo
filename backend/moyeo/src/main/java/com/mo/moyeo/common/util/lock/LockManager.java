package com.mo.moyeo.common.util.lock;

import com.mo.moyeo.common.aspect.TransactionExecutor;
import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class LockManager {

    private static final long DEFAULT_WAIT_TIME = 5;
    private static final long DEFAULT_LEASE_TIME = 10;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    private static final String REDISSON_LOCK_PREFIX = "lock:";
    private static final String BOX_LOCK_PREFIX = "box:";

    private final RedissonClient redissonClient;
    private final TransactionExecutor transactionExecutor;

    /**
     * 일반 락 획득
     */
    public <T> T executeWithLock(String[] keys, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) {
        // 1. 락 배열 생성
        RLock[] locks = Arrays.stream(keys)
                .map(key -> redissonClient.getLock(REDISSON_LOCK_PREFIX + key))
                .toArray(RLock[]::new);

        // 2. 멀티락 생성
        RLock multiLock = new RedissonMultiLock(locks);

        // 로그 출력용
        String threadName = Thread.currentThread().getName();
        List<String> keyNames = Arrays.stream(locks).map(RLock::getName).toList();

        // 3. 락 획득 시도
        boolean locked = false;
        try {
            log.debug("락 획득 시도 - keys: {}, thread: {}", keyNames, threadName);
            locked = multiLock.tryLock(waitTime, leaseTime, timeUnit);
            if (!locked) {
                log.warn("락 획득 실패 - keys: {}, thread: {}", keyNames, threadName);
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

            log.info("락 획득 성공 - keys: {}, thread: {}", keyNames, threadName);

            // 4. 로직 실행
            try {
                return transactionExecutor.execute(callback);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            log.error("락 대기 중 인터럽트 발생 - keys: {}, thread: {}", keyNames, threadName);
            throw new RuntimeException(e);
        } finally {
            // 5. 락 해제
            if (locked) {
                try {
                    multiLock.unlock();
                    log.info("락 해제 성공 - keys: {}, thread: {}", keyNames, threadName);
                } catch (IllegalMonitorStateException e) {
                    log.warn("락 이미 해제됨");
                }
            }
        }
    }


    public <T> T executeWithLock(String[] keys, LockCallback<T> callback) {
        return executeWithLock(keys, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, DEFAULT_TIME_UNIT, callback);
    }

}
