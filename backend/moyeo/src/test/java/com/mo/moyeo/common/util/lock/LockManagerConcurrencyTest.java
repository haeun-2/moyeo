package com.mo.moyeo.common.util.lock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class LockManagerConcurrencyTest {

    @Autowired LockManager lockManager;

    @Test
    void testConcurrentLockingWithCounter() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Counter counter = new Counter(0);

        String[] lockKeys = {"COUNTER_LOCK"};

        for (int i = 0; i < threadCount; i++) {
            int threadNum = i;
            executor.submit(() -> {
                try {
                    lockManager.executeWithLock(lockKeys, 10, 0, TimeUnit.SECONDS, () -> {
                        // 락 획득 후 안전하게 감소
                        counter.increment();
                        return null;
                    });
                } catch (Throwable e) {
                    System.err.println("Thread " + threadNum + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 최종 값 검증
        System.out.println("Final counter value: " + counter.getValue());
        Assertions.assertEquals(counter.getValue(), threadCount);
    }

}



