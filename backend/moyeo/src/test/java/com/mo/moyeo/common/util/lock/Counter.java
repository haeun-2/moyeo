package com.mo.moyeo.common.util.lock;

public class Counter {

    private int value;

    public Counter(int initial) {
        this.value = initial;
    }

    public void increment() {
        value++;
    }

    public int getValue() {
        return value;
    }
}
