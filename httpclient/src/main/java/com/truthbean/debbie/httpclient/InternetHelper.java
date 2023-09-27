/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.util.NetWorkUtils;

import java.util.regex.Matcher;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-02 14:05.
 */
public class InternetHelper extends NetWorkUtils {

    private final HttpConnectionHandler handler;

    public InternetHelper(HttpClientConfiguration configuration) {
        super();
        this.handler = new HttpConnectionHandler(configuration);
    }

    public InternetHelper(HttpConnectionHandler handler) {
        super();
        this.handler = handler;
    }

    /**
     * @return 外网IP
     */
    public String getInternetIp() {
        String remoteUrl = "http://icanhazip.com";
        String ip = getInternetIp(remoteUrl);
        if (ip != null) {
            return ip;
        }

        remoteUrl = "http://ip.3322.net";
        ip = getInternetIp(remoteUrl);
        if (ip != null) {
            return ip;
        }

        remoteUrl = "http://myip.dnsomatic.com";
        ip = getInternetIp(remoteUrl);
        if (ip != null) {
            return ip;
        }

        return getLocalIpv4Address().getHostAddress();
    }

    private String getInternetIp(String remoteUrl) {
        try {
            String ip = this.handler.get(remoteUrl);
            if (ip != null) {
                LOGGER.trace(() -> "internet ip: " + ip);
                Matcher matcher = IPV4_PATTERN.matcher(ip);
                if (matcher.find()) {
                    return matcher.group();
                }
            }
            return null;
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(InternetHelper.class);
}
