package com.truthbean.debbie.example.filter;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.mvc.request.filter.Filter;
import com.truthbean.debbie.mvc.request.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;

@Filter(name = "exampleFilter1111", value = "/*", order = 2)
public class ExampleFilter2 implements RouterFilter {
    @Override
    public boolean doFilter(RouterRequest request, RouterResponse response) {
        System.out.println("doFilter2.....");
        response.setResponseType(MediaType.APPLICATION_JSON_UTF8);
        response.setContent("{\"status\":\"403\"}");
        return false;
    }
}
