package jp.or.venuspj.remoting.urlBase;

import jp.or.venuspj.remoting.common.client.RemotingConnector;
import jp.or.venuspj.remoting.urlBase.config.UrlBaseServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UrlBaseConnector implements RemotingConnector {
    protected UrlBaseServiceConfig urlBaseServiceConfig;

    @Autowired
    public UrlBaseConnector(UrlBaseServiceConfig anUrlBaseServiceConfig) {
        urlBaseServiceConfig = anUrlBaseServiceConfig;
    }
}
