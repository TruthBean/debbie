package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.mvc.request.QueryParameter;

import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-03 12:07
 */
public class AbstractRouterTest<C extends List> {

    @Router(urlPatterns = "/hehe")
    public CharSequence testPathAttribute0(
            @QueryParameter(name = "hehe", require = false, defaultValue = "0") C id) {
        return "<html><head><title>" + id + "</title></head><body><p>this is a path attribute test</p></body></html>";
    }
}
