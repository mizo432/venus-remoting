package jp.or.venuspj.remoting.json.client;

import jp.or.venuspj.remoting.urlBase.UrlBaseConnector;
import jp.or.venuspj.remoting.urlBase.config.UrlBaseServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

public class JsonConnector extends UrlBaseConnector {

    @Autowired
    public JsonConnector(UrlBaseServiceConfig anUrlBaseServiceConfig) {
        super(anUrlBaseServiceConfig);
    }

    @Override
    public Object invoke(String name, Method method, Object[] args) throws Throwable{
        return null;
    }

}
