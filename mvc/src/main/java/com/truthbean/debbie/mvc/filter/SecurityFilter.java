/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019-11-24 20:58.
 */
public class SecurityFilter implements RouterFilter, Closeable {

    private MvcConfiguration configuration;

    private final ThreadLocal<Boolean> attacked = new ThreadLocal<>();

    @Override
    public SecurityFilter setMvcConfiguration(MvcConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public boolean preRouter(RouterRequest request, RouterResponse response) {
        if (!this.configuration.isEnableSecurity()) return true;
        String userAgent = request.getHeader().getHeader("user-agent");
        var attacked = userAgent != null && (
                userAgent.startsWith("$")
                        || userAgent.startsWith("Java") || userAgent.startsWith("Jakarta")
                || userAgent.startsWith("Ruby")
                        || userAgent.contains("IDBot") || userAgent.contains("Wget")
                || userAgent.contains("id-search") || userAgent.contains("User-Agent")
                || userAgent.contains("ConveraCrawler")
                        || userAgent.contains("Mozilla")
                || userAgent.contains("libwww") || userAgent.contains("lwp-trivial")
                || userAgent.contains("curl") || userAgent.contains("PHP/")
                || userAgent.contains("urllib") || userAgent.contains("GT:WWW")
                || userAgent.contains("Snoopy") || userAgent.contains("MFC_Tear_Sample")
                || userAgent.contains("HTTP::Lite") || userAgent.contains("PHPCrawl")
                || userAgent.contains("URI::Fetch") || userAgent.contains("Zend_Http_Client")
                || userAgent.contains("http client") || userAgent.contains("PECL::HTTP")
                || userAgent.contains("Fetch API Request") || userAgent.contains("PleaseCrawl")
                || userAgent.contains("TurnitinBot") || userAgent.contains("python-requests")
                || userAgent.contains("Python-urllib") || userAgent.contains("postman")
                || userAgent.contains("CorporateNewsSearchEngine") || userAgent.contains("libwww-perl")
                || userAgent.contains("rogerbot") || userAgent.contains("Microsoft URL Control")
                || "-".equalsIgnoreCase(userAgent) || "MSIE 6.0".equalsIgnoreCase(userAgent)
                || "Mozilla/4.0 (compatible; Advanced Email Extractor v2.xx)".equalsIgnoreCase(userAgent)
                || "Mozilla/4.0 (compatible; Iplexx Spider/1.0 http://www.iplexx.at)".equalsIgnoreCase(userAgent)
                || "Mozilla/5.0 (Version: xxxx Type:xx)".equalsIgnoreCase(userAgent)
                || "MVAClient".equalsIgnoreCase(userAgent) || "MJ12bot".equalsIgnoreCase(userAgent)
                || "spider-ads".equalsIgnoreCase(userAgent) || "bakey".equalsIgnoreCase(userAgent)
                || "NameOfAgent (CMS Spider)".equalsIgnoreCase(userAgent) || "PBrowse 1.4b".equalsIgnoreCase(userAgent)
                || "Poirot".equalsIgnoreCase(userAgent) || "searchbot admin@google.com".equalsIgnoreCase(userAgent)
                || "sogou develop spider".equalsIgnoreCase(userAgent) || "WEP Search 00".equalsIgnoreCase(userAgent)
                );
        LOGGER.trace(() -> "attacked: " + attacked);
        this.attacked.set(attacked);
        return !attacked;
    }

    @Override
    public Boolean postRouter(RouterRequest request, RouterResponse response) {
        Boolean bool = this.attacked.get();
        if (bool != null && bool) {
            response.setStatus(HttpStatus.FORBIDDEN);
            response.setContent("You are banned from this site.  Please contact via a \n" + "different client configuration if you believe that this is a mistake.");
            return true;
        } else
            return bool;
    }

    @Override
    public void close() throws IOException {
        this.attacked.remove();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);
}
