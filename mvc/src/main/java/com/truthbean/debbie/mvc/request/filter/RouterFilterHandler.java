package com.truthbean.debbie.mvc.request.filter;

import com.truthbean.debbie.core.bean.BeanFactory;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/25 15:57.
 */
public class RouterFilterHandler {
    private RouterFilterInfo filterInfo;


    public RouterFilterHandler(RouterFilterInfo routerFilterInfo) {
        this.filterInfo = routerFilterInfo;
    }

    public Boolean doFilter(RouterRequest routerRequest, RouterResponse routerResponse) {
        String url = routerRequest.getUrl();

        Class<? extends RouterFilter> routerFilterType = filterInfo.getRouterFilterType();
        RouterFilter routerFilter = BeanFactory.factory(routerFilterType);

        List<String> rawUrlPattern = filterInfo.getRawUrlPattern();
        for (String s : rawUrlPattern) {
            if (s.equals(url)) {
                return routerFilter.doFilter(routerRequest, routerResponse);
            }
        }

        List<Pattern> urlPattern = filterInfo.getUrlPattern();
        for (Pattern pattern : urlPattern) {
            if (pattern.matcher(url).find()) {
                return routerFilter.doFilter(routerRequest, routerResponse);
            }
        }

        return null;
    }

}
