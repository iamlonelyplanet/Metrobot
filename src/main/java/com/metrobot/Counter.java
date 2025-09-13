package com.metrobot;

import java.util.Arrays;
import java.util.Map;

public class Counter {
    private String name;
    private int count;

    public Counter(String counterName) {
        this(counterName, 0);
    }

    public Counter(String counterName, int counter) {
        this.name = counterName;
        this.count = counter;
    }

    public void plusOne() {
        count++;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
