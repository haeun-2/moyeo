package com.mo.moyeo.common.aspect;

import com.mo.moyeo.common.util.lock.LockCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionExecutor {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T execute(LockCallback<T> callback) throws Throwable {
        return callback.execute();
    }

}
