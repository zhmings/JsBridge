package dev.xesam.android.web.jsbridge.client;

import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;

import dev.xesam.android.web.jsbridge.TransactInfo;
import dev.xesam.android.web.jsbridge.JsBridge;
import dev.xesam.android.web.jsbridge.Marshallable;

/**
 * Created by xesamguo@gmail.com on 16-4-7.
 */
public class ClientProxy {

    private Map<Long, ClientCallback<?>> callbacks = new HashMap<>();
    private JsBridge mJsBridge;
    private JsExecutor mJsExecutor;

    public ClientProxy(JsBridge mJsBridge) {
        this.mJsBridge = mJsBridge;
        this.mJsExecutor = new JsExecutor(mJsBridge);
    }

    public void transact(String script) {
        mJsExecutor.transact(script);
    }

    public void transact(TransactInfo transactInfo, Marshallable invokeParam, ClientCallback<?> clientCallback) {
        if (clientCallback != null) {
            final long callbackId = SystemClock.elapsedRealtime();
            callbacks.put(callbackId, clientCallback);
            transactInfo.setCallbackId(callbackId);
        }
        mJsExecutor.transact(transactInfo, invokeParam);
    }

    /**
     * js -> java ： 回调 java 方法
     */
    public void dispatchClientCallback(TransactInfo transactInfo, String paramMarshalling) {
        ClientCallback clientCallback = callbacks.get(transactInfo.getInvokeId());
        if (clientCallback != null) {
            clientCallback.onReceiveResult(transactInfo.getInvokeName(), clientCallback.getResult(paramMarshalling));
            callbacks.remove(transactInfo.getInvokeId());
        }
    }
}
