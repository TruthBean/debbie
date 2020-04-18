package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.bean.BeanFactoryHandler;
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
    private final RouterFilterInfo filterInfo;

    private final RouterFilter routerFilter;


    public RouterFilterHandler(RouterFilterInfo routerFilterInfo, BeanFactoryHandler handler) {
        this.filterInfo = routerFilterInfo;

        Class<? extends RouterFilter> routerFilterType = filterInfo.getRouterFilterType();
        RouterFilter routerFilter = filterInfo.getFilterInstance();
        if (routerFilter == null)
            routerFilter = handler.factory(routerFilterType);
        routerFilter.setMvcConfiguration(this.filterInfo.getConfiguration());
        this.routerFilter = routerFilter;
    }

    public Boolean preRouter(RouterRequest routerRequest, RouterResponse routerResponse) {
        String url = routerRequest.getUrl();

        List<String> rawUrlPattern = filterInfo.getRawUrlPattern();
        for (String s : rawUrlPattern) {
            if (s.equals(url)) {
                return routerFilter.preRouter(routerRequest, routerResponse);
            }
        }

        List<Pattern> urlPattern = filterInfo.getUrlPattern();
        for (Pattern pattern : urlPattern) {
            if (pattern.matcher(url).find()) {
                return routerFilter.preRouter(routerRequest, routerResponse);
            }
        }

        return null;
    }

    public Boolean postRouter(RouterRequest routerRequest, RouterResponse routerResponse) {
        return routerFilter.postRouter(routerRequest, routerResponse);
    }

}
