package com.example.cliente.log;

import brave.Tracing;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class LogSplunk {

    @Autowired
    private Tracing tracing; //Tracer

    private Map<String, Log> mapReqResp = new HashMap<>();
    private Map<String, Log.RouteStep> mapSteps = new HashMap<>();

    @Value("${spring.application.name}")
    private String applicationName;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void pointcutController() {
    }

    @Before("pointcutController()")
    public void processaAntesController(final JoinPoint joinPoint) throws IOException {
        Log logOut = new Log();
        logOut.setApplicationName(applicationName);
        logOut.setMethodExecution(joinPoint.toString());
        final HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logOut.getRequest().setHttpMethod(request.getMethod());
        logOut.getRequest().setHttpUrl(request.getRequestURL().toString());
        Map<String, String> headers = this.getRequestHeaders(request);
        logOut.getRequest().setHeaders(headers);
        //logOut.getRequest().setBody(IOUtils.toString(request.getReader()));
        Object body = joinPoint.getArgs() != null && joinPoint.getArgs().length > 0 ? joinPoint.getArgs()[0] : "";
        logOut.getRequest().setBody(body);
        //logOut.setTraceId(UUID.randomUUID().toString());
        mapReqResp.put(this.getTraceId(), logOut);
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        //Stream.of(request.getHeaderNames())//.forEach(System.out::print);
        //        .collect(Collectors.toMap(h -> h.hasMoreElements(), h -> request.getHeader(h)));
        Map<String, String> headers = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            headers.put(key, value);
        }
        return headers;
    }

    @AfterReturning(pointcut = "pointcutController()", returning = "retorno")
    public void processaDepoisController(final JoinPoint joinPoint, final Object retorno) throws JsonProcessingException {
        final HttpServletResponse response = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getResponse();
        Log logOut = mapReqResp.get(this.getTraceId());
        logOut.getResponse().setHttpStatus(String.valueOf(response.getStatus()));
        Map<String, String> headers = response.getHeaderNames().stream()
                .collect(Collectors.toMap(h -> h, h -> response.getHeader(h)));
        logOut.getResponse().setHeaders(headers);
        logOut.getResponse().setBody(retorno);
        logOut.setLevel(response.getStatus() > 199 && response.getStatus() < 300 ? "SUCCESS" : "FAILED");
        logOut.setTraceId(this.getTraceId());
        System.out.println(objectMapper().writeValueAsString(logOut));
    }

    //@Pointcut("execution(* br.com.groot.galaxy.commons.config.feign.ApacheHttpClient.*(..))")
    //@Pointcut("execution(* org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient.execute(..))")
    //@Pointcut("within(@org.springframework.cloud.openfeign.FeignClient *)")
    //@Pointcut("execution(* feign.httpclient.ApacheHttpClient.*(..))")
    @Pointcut("execution(* com.example.cliente.config.ApacheHttpClient.*(..))")
    public void feignDefaultClientPointcut() {
    }

    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }

    @Before("feignDefaultClientPointcut()")
    public synchronized void processaAntesFeign(final JoinPoint pjp) {

//        Class clazz = pjp.getTarget().getClass();
//        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
//        Method method = methodSignature.getMethod();
//        GetMapping methodPostMapping = method.getAnnotation(GetMapping.class);
//        RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
//        String url = getPostUrl(requestMapping, methodPostMapping);
//        Log.RouteStep step = new Log.RouteStep();
//        Log.Request request = new Log.Request();
//        request.setHttpUrl(url);
//        step.setRequest(request);
//        logOut.getSteps().add(step);

        for (final Object obj : pjp.getArgs()) {
            if (obj instanceof Request) {

                final Request request = (Request) obj;

                Log.Request requestStep = new Log.Request();
                requestStep.setHttpMethod(request.httpMethod().name());
                requestStep.setHttpUrl(request.url());
                request.headers().forEach((key, value) -> value.forEach(v -> requestStep.getHeaders().put(key, v)));
                Request.Body body = Request.Body.encoded(request.body(), StandardCharsets.UTF_8);
                requestStep.setBody(body);

                Log.RouteStep step = new Log.RouteStep();
                step.setRequest(requestStep);
                mapSteps.put(request.toString(), step);
            }
        }
    }

    @SuppressWarnings("unused")
    @AfterReturning(pointcut = "feignDefaultClientPointcut()", returning = "retorno")
    public synchronized void processaAfterReturningFeignInicio(final JoinPoint jp, final Object retorno) {
        if (retorno instanceof Response) {
            final Response response = (Response) retorno;
            Log.RouteStep routeStep = mapSteps.get(response.request().toString());
            routeStep.getResponse().setHttpStatus(String.valueOf(response.status()));

            //final LogBuilder logBuilderFeign = mapFeignLoggers.get(response.request().toString());
            //logBuilderFeign.add("ResponseStatus: ", String.valueOf(response.status()));
            //final LogParameters logParamHeader = LogParameters.create("responseHeader");
            response.headers().forEach((key, value) -> value.forEach(v -> {
                routeStep.getResponse().getHeaders().put(key, v);
            }));
            final String traceId = getTraceId();
            //logBuilderFeign.add(logParamHeader);
            //mapFeignLoggers.remove(response.request().toString());
            //mapFeignLoggers.put(new StringBuilder(String.valueOf(response.headers().get("date"))
            //                + String.valueOf(response.headers().get("set-cookie")) + traceId).toString().replaceAll("\\[", "").replaceAll("\\]", ""),
            //        logBuilderFeign);

            Log log = mapReqResp.get(traceId);
            log.getSteps().add(routeStep);
            mapReqResp.put(traceId, log);
        }
    }

//    @AfterReturning(pointcut = "feignClientPointcut()", returning = "retorno")
//    public void processaAfterReturningFeignFim(final JoinPoint jp, final Object retorno) {
//        Log.Response responseStep = new Log.Response();
//        responseStep.setBody(retorno);
//        Log.RouteStep step = new Log.RouteStep();
//        step.setResponse(responseStep);
//        logOut.getSteps().add(step);
//    }

    private String getTraceId() {
        return this.tracing.currentTraceContext().get().traceIdString();
        //return UUID.randomUUID().toString();
    }

}