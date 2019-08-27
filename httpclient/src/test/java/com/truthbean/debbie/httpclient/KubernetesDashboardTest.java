package com.truthbean.debbie.httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.truthbean.debbie.util.JacksonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class KubernetesDashboardTest {
    private HttpClientHandler httpClientHandler;

    private String baseUrl = "https://192.168.1.11:30443";
    private String dashboardToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi11c2VyLXRva2VuLXFzZm5jIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImFkbWluLXVzZXIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJjZmRlOTM3NC0wZTYyLTQ2NzEtYTllNC1mYzcyNGY4ZmM0M2QiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06YWRtaW4tdXNlciJ9.RZvSiPof_73s-Lt_mnDs7NNkH4b5ahwWY4pnnxi1aP4dcSscGQ0RFycQ691jlJBKogxjtLAL8MOoT6RR0yWQMN0mmq6oyn49Wyv62Qb_TRQbjOMaqlYRNao5dzX2B0IIpUGPKa7ScS0ZejmknLKq6mGIeBd72iYfZk01hjb63X-AqgJ20lAGoY3Rb1KaK0zz1fuWuhVJgzOM1_fxqCt8t-Y7ngxLM7Zpk7VtJeGxGuCOjuajheXUXAiWZ3I59Kj0iVfiy4v3un60_twp-75Sm63a2HfGHuCQN439a_UQFlyEMCRTuWlrEz3-RQkVSTh58VgzvvgnGFGJC4gVZPXS8g";

    @BeforeEach
    public void before() {
        var properties = new HttpClientProperties();
        httpClientHandler = new HttpClientHandler(properties.toConfiguration());
    }

    @Test
    public void loginWithCsrftoken() {
        String url = baseUrl + "/api/v1/csrftoken/login";
        String token = null;

        String tokenStr = httpClientHandler.get(url);
        JsonNode tokenNode = JacksonUtils.toJsonNode(tokenStr);
        if (tokenNode != null && tokenNode.has("token")) {
            token = tokenNode.get("token").asText();
        }
        System.out.println(token);
    }

    @Test
    public void loginK8sDashboard() {
        String token = "EIysyUjpUe-pgQX9aFqJjUsX_Ek:1566822212029";
        String url = baseUrl + "/api/v1/login";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-CSRF-TOKEN", token);
        String jsonContent = "{\"username\":\"\",\"password\":\"\",\"token\":\"" + dashboardToken + "\",\"kubeConfig\":\"\"}";

        String jweToken = null;

        String result = httpClientHandler.post(url, jsonContent, headers);
        JsonNode jsonNode = JacksonUtils.toJsonNode(result);
        if (result != null && jsonNode.has("jweToken")) {
            jweToken = jsonNode.get("jweToken").asText();
        }

        System.out.println(jweToken);
    }

    @Test
    public void testK8s() {
        var response = httpClientHandler.get(baseUrl + "/api/v1/settings/global");
        System.out.println(response);
    }
}
