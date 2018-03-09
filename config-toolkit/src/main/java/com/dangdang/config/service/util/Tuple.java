package com.dangdang.config.service.util;

public class Tuple<F, S> {

    private F first;

    private S second;

    public Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
