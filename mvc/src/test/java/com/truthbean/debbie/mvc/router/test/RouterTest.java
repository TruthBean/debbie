/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router.test;

import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.router.Router;

import java.util.ArrayList;
import java.util.List;

@Router
public class RouterTest extends AbstractRouterTest<ArrayList> {

    @Router(urlPatterns = "/{hehe}")
    public String testPathAttribute0(
            @RequestParameter(name = "hehe", paramType = RequestParameterType.PATH, require = false, defaultValue = "0")
                    ArrayList id) {
        return "<html><head><title>" + id + "</title></head><body><p>this is a path attribute test</p></body></html>";
    }

    @Router(urlPatterns = "/{id}/{id}")
    public String testPathAttribute(
            @RequestParameter(name = "id", paramType = RequestParameterType.PATH, require = false, defaultValue = "0")
                    List<String> id) {
        return "<html><head><title>" + id + "</title></head><body><p>this is a path attribute test</p></body></html>";
    }

    @Router(urlPatterns = "/id/hello")
    public String testRawUrl() {
        return "hello";
    }

    @Router(urlPatterns = "/id/{hello}/{hello:\\d+}")
    public Integer testPathAttribute1(
            @RequestParameter(name = "hello", paramType = RequestParameterType.PATH, require = false, defaultValue = "0") Integer hello) {
        return hello;
    }

    @Router(urlPatterns = "/{he:[\\d]+}-{llo}")
    public Integer testPathAttribute2(
            @RequestParameter(name = "he", paramType = RequestParameterType.PATH) Integer he,
            @RequestParameter(name = "llo", paramType = RequestParameterType.PATH) Integer llo
    ) {
        return he + llo;
    }


    @Router(urlPatterns = "/hehe/{he:\\d+}-{llo}")
    public Integer testPathAttribute3(
            @RequestParameter(name = "he", paramType = RequestParameterType.PATH) Integer he,
            @RequestParameter(name = "llo", paramType = RequestParameterType.PATH) Integer llo
    ) {
        return he + llo;
    }

    // NOTE: 先留个坑，动态url尽量放到类的底下，这样才能最后才判断时候合适....

    @Router(urlPatterns = "/{hehe}/{he:\\d+}-{llo}")
    public Integer testPathAttribute4(
            @RequestParameter(name = "hehe", paramType = RequestParameterType.PATH) String hehe,
            @RequestParameter(name = "he", paramType = RequestParameterType.PATH) Integer he,
            @RequestParameter(name = "llo", paramType = RequestParameterType.PATH) Integer llo
    ) {
        return he + llo;
    }

    @Router(urlPatterns = "/{he}/{he}/{he1:\\d+}-{llo}")
    public Integer testPathAttribute5(
            @RequestParameter(name = "he", paramType = RequestParameterType.PATH) List<String> list,
            @RequestParameter(name = "he1", paramType = RequestParameterType.PATH) Integer he,
            @RequestParameter(name = "llo", paramType = RequestParameterType.PATH) Integer llo
    ) {
        return he + llo;
    }

    @Router(urlPatterns = "/{he}/{he}/{he}-{llo}")
    public String testPathAttribute6(
            @RequestParameter(name = "he", paramType = RequestParameterType.PATH) List<String> list,
            @RequestParameter(name = "llo", paramType = RequestParameterType.PATH) Integer llo
    ) {
        return list.toString() + llo;
    }

}
