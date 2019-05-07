package com.truthbean.debbie.example.filter;

import com.truthbean.debbie.mvc.filter.Filter;
import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;

@Filter(name = "exampleFilter1111", value = "/*", order = 2)
public class ExampleFilter2 implements RouterFilter {
    @Override
    public boolean doFilter(RouterRequest request) {
        System.out.println("doFilter2.....");
        return true;
    }
}
