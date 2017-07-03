package jp.or.venuspj.remoting.common.client;

import java.lang.reflect.Method;

public interface RemotingConnector {
    Object invoke(String name, Method method, Object[] args) throws Throwable;
}
