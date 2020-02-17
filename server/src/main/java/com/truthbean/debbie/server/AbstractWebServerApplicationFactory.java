package com.truthbean.debbie.server;

import com.truthbean.debbie.boot.AbstractApplicationFactory;
import com.truthbean.debbie.net.NetWorkUtils;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.util.List;

/**
 * @author truthbean
 * @since 0.0.2
 */
public abstract class AbstractWebServerApplicationFactory extends AbstractApplicationFactory {

    @Override
    public boolean isWeb() {
        return true;
    }

    protected void printlnWebUrl(Logger logger, int port) {
        logger.trace("before print....");
        List<InetAddress> ipv4LocalAddress = NetWorkUtils.getAllIpv4LocalAddress();
        logger.info("application start with http://[::1]:" + port);
        logger.info("application start with http://127.0.0.1:" + port);
        for (InetAddress localAddress : ipv4LocalAddress) {
            logger.info("application start with http://" + localAddress.getHostAddress() + ":" + port);
        }
    }
}
