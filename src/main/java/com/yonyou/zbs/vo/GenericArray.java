package com.yonyou.zbs.vo;

public class GenericArray<T> {
    private final Object[] arr;

    public GenericArray(int n) {
        this.arr = new Object[n];
    }

    public void set(int i, T o) {
        this.arr[i] = o;
    }

    @SuppressWarnings("unchecked")
    public T get(int i) {
        return (T) this.arr[i];
    }
}