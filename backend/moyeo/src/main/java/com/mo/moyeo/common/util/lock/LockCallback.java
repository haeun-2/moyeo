package com.mo.moyeo.common.util.lock;

@FunctionalInterface
public interface LockCallback<T> {
    T execute() throws Throwable;
}
