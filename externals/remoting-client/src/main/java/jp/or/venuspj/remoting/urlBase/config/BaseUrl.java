package jp.or.venuspj.remoting.urlBase.config;

import java.net.URL;

public class BaseUrl {
    URL value;

    BaseUrl() {
    }

    public BaseUrl(URL aValue) {
        value = aValue;
    }

    public URL asUrl() {
        return value;
    }
}
