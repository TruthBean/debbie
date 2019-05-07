package com.truthbean.debbie.example.router;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.watcher.Watcher;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.response.view.StaticResourcesView;
import com.truthbean.debbie.mvc.router.Router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public Map<String, Object> helloXml(@RequestParameter(name = "key", paramType = RequestParameterType.QUERY) String key,
                                        @RequestParameter(name = "value", paramType = RequestParameterType.QUERY) List<Integer> value) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    @Router(path = "/json", method = {HttpMethod.POST, HttpMethod.GET}, responseType = MediaType.APPLICATION_JSON_UTF8)
    public Map<String, Object> helloJson(@RequestParameter(name = "key", paramType = RequestParameterType.PARAM) String key,
                                         @RequestParameter(name = "value", paramType = RequestParameterType.PARAM) List<Integer> value) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    @Router(path = "/matrix", method = HttpMethod.POST, responseType = MediaType.APPLICATION_JSON_UTF8)
    public Map<String, Object> helloMatrix(@RequestParameter(name = "key", paramType = RequestParameterType.MATRIX) String key,
                                           @RequestParameter(name = "value", paramType = RequestParameterType.MATRIX) List<Integer> value) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    @Router(path = "/body/xml", method = HttpMethod.GET, responseType = MediaType.APPLICATION_JSON_UTF8)
    public Map<String, Object> helloBodyXml(@RequestParameter(name = "key", paramType = RequestParameterType.QUERY) String key,
                                            @RequestParameter(name = "value", paramType = RequestParameterType.BODY,
                                                    bodyType = MediaType.APPLICATION_XML) Map<Object, Object> value) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    @Router(path = "/body/json", method = HttpMethod.POST, responseType = MediaType.APPLICATION_XML_UTF8)
    public Map<String, Object> helloBodyJson(@RequestParameter(name = "key", paramType = RequestParameterType.QUERY) String key,
                                             @RequestParameter(name = "value", paramType = RequestParameterType.BODY,
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

    @Router(path = "/{id}/{id}")
    public String testPathAttribute(
            @RequestParameter(name = "id", paramType = RequestParameterType.PATH, require = false, defaultValue = "0")
                    List<Integer> id) {
        return "<html><head><title>" + id + "</title></head><body><p>this is a path attribute test</p></body></html>";
    }

    @Router(path = "/inner")
    public String testPathAttribute(
            @RequestParameter(name = "token", paramType = RequestParameterType.INNER, require = false, defaultValue = "")
                    UUID id) {
        return "<html><head><title>" + id.toString() + "</title></head><body><p>this is a path attribute test</p></body></html>";
    }
}
