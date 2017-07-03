package jp.or.venuspj.remoting.urlBase.config;

public class UrlBaseServiceConfig {
    BaseUrl baseUrl;

    UrlBaseServiceConfig() {

    }

    public UrlBaseServiceConfig(BaseUrl aBaseUrl) {
        baseUrl = aBaseUrl;
    }

    public BaseUrl baseUrl() {
        return baseUrl;
    }
}
