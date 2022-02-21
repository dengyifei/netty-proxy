package com.efei.proxy.common.face;

@FunctionalInterface
public interface CallBack<T> {

    void accept(T t);
}
