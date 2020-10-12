package com.testproxyservertoserver;

import java.util.EventListener;

public interface TestConnectEventListener<T> extends EventListener {


    public void onConnectSuccess(T c);
}
