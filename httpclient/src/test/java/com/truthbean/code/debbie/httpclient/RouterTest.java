package com.truthbean.code.debbie.httpclient;

import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.mvc.request.RequestParam;
import com.truthbean.code.debbie.mvc.request.RequestParamType;
import com.truthbean.code.debbie.mvc.router.Router;

@Router
public interface RouterTest {

    <T> T login(
            @RequestParam(paramType = RequestParamType.BODY, bodyType = MediaType.APPLICATION_JSON_UTF8) String body
    );
}
