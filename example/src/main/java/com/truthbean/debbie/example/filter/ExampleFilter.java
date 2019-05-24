package com.truthbean.debbie.example.filter;

import com.truthbean.debbie.mvc.request.filter.Filter;
import com.truthbean.debbie.mvc.request.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;

import java.util.UUID;

@Filter(name = "exampleFilter2222", value = "/*", order = 2)
public class ExampleFilter implements RouterFilter {

    @Override
    public boolean doFilter(RouterRequest request, RouterResponse response) {
        System.out.println("doFilter.....");
        request.addAttribute("token", UUID.randomUUID());
        return true;
    }
}
