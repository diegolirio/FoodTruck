package com.example.cliente.log;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Log {

    private String level;
    private String traceId; // Request Steps
    private String spanId; // Request unique
    private String applicationName;
    private String methodExecution;
    private Request request = new Request();
    private Response response = new Response();
    private List<RouteStep> steps = new ArrayList<>();

    @Data
    public static class RouteStep {
        private Request request = new Request();
        private Response response = new Response();
    }

    @Data
    public static class Request {
        private String httpMethod;
        private String httpUrl;
        private Object body;
        private Map<String, String> headers = new HashMap<>();
    }

    @Data
    public static class Response {
        private String httpStatus;
        private Object body;
        private Map<String, String> headers = new HashMap<>();
    }

}

