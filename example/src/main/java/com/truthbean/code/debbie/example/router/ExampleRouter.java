package com.truthbean.code.debbie.example.router;

import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.watcher.Watcher;
import com.truthbean.code.debbie.mvc.request.HttpMethod;
import com.truthbean.code.debbie.mvc.request.RequestParam;
import com.truthbean.code.debbie.mvc.request.RequestParamType;
import com.truthbean.code.debbie.mvc.response.view.StaticResourcesView;
import com.truthbean.code.debbie.mvc.router.Router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-14 15:07.
 */
@Watcher
@Router("example")
public class ExampleRouter {

    @Router(path = "/index", method = HttpMethod.GET, responseType = MediaType.TEXT_PLAIN_UTF8)
    public String helloWorld() {
        return "{\"message\":\"hello world\", \"method\":\"GET\"}";
    }

    @Router(path = "/xml", method = HttpMethod.GET, responseType = MediaType.APPLICATION_XML_UTF8)
    public Map<String, Object> helloXml(@RequestParam(name = "key", paramType = RequestParamType.QUERY) String key,
                        @RequestParam(name = "value", paramType = RequestParamType.QUERY) List<Integer> value) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    @Router(path = "/json", method = HttpMethod.POST, responseType = MediaType.APPLICATION_JSON_UTF8)
    public Map<String, Object> helloJson(@RequestParam(name = "key", paramType = RequestParamType.PARAM) String key,
                                        @RequestParam(name = "value", paramType = RequestParamType.PARAM) List<Integer> value) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    @Router(path = "/body/xml", method = HttpMethod.GET, responseType = MediaType.APPLICATION_JSON_UTF8)
    public Map<String, Object> helloBodyXml(@RequestParam(name = "key", paramType = RequestParamType.QUERY) String key,
                                            @RequestParam(name = "value", paramType = RequestParamType.BODY,
                                                    bodyType = MediaType.APPLICATION_XML) Map<Object, Object> value) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    @Router(path = "/body/json", method = HttpMethod.POST, responseType = MediaType.APPLICATION_XML_UTF8)
    public Map<String, Object> helloBodyJson(@RequestParam(name = "key", paramType = RequestParamType.QUERY) String key,
                                            @RequestParam(name = "value", paramType = RequestParamType.BODY,
                                                    bodyType = MediaType.APPLICATION_JSON) Map<Object, Object> value) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    @Router(path = "/index.png", hasTemplate = true, responseType = MediaType.IMAGE_PNG)
    public StaticResourcesView resourceJpg() {
        var view = new StaticResourcesView();
        view.setTemplate("index");
        view.setSuffix(".png");
        view.setText(false);
        return view;
    }

    @Router(path = "/index.html", hasTemplate = true, responseType = MediaType.TEXT_HTML_UTF8)
    public StaticResourcesView resource() {
        return new StaticResourcesView();
    }

    @Router(path = "/test.html", responseType = MediaType.TEXT_HTML_UTF8)
    public String reponseHtml() {
        return "<html><head><title>666</title></head><body><p>this is a demo</p></body></html>";
    }

    @Router(path = "/alias", hasTemplate = true)
    public StaticResourcesView resourceAlias() {
        var view = new StaticResourcesView();
        view.setTemplate("index");
        view.setSuffix(".html");
        view.setText(true);
        return view;
    }
}
