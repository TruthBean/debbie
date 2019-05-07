package com.truthbean.debbie.example.filter;

import com.truthbean.debbie.mvc.filter.Filter;
import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;

import java.util.UUID;

@Filter(name = "exampleFilter2222", value = "/*", order = 1)
public class ExampleFilter implements RouterFilter {
    @Override
    public boolean doFilter(RouterRequest request) {
        System.out.println("doFilter.....");
        request.addAttribute("token", UUID.randomUUID());
        return true;
    }
}
