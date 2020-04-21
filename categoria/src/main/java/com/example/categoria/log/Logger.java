package com.example.categoria.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
class Logger {

    private Map<String, Log> mapReqResp = new HashMap<>();
    //private Map<String, Log.RouteStep> mapSteps = new HashMap<>();

    @Value("${spring.application.name}")
    private String applicationName;

    @Pointcut("within(@com.example.categoria.log.LogDownstream *)")
    public void pointcutRequest() {
    }

    @Before("pointcutRequest()")
    public void beforeServerRequest(final JoinPoint joinPoint) throws IOException {
        if (isNotServerRequest(joinPoint)) {
            return;
        }

        ServerRequest serverRequest = (ServerRequest) joinPoint.getArgs()[0];
        ServerHttpRequest request = serverRequest.exchange().getRequest();

        //String s = resolveBodyFromRequest(request);

        Log logOut = new Log();
        logOut.setApplicationName(applicationName);
        logOut.setMethodExecution(joinPoint.toString());
        logOut.getRequest().setHttpMethod(request.getMethodValue());
        logOut.getRequest().setHttpUrl(request.getURI().toString());
        request.getHeaders().entrySet()
                .forEach(f -> logOut.getRequest().getHeaders().put(f.getKey(), f.getValue().get(0)));

        // TODO RequestBody ERROR
        //serverRequest.bodyToMono(Object.class).subscribe(logOut.getRequest()::setBody);

        mapReqResp.put(this.getTraceId(), logOut);

        System.out.println(objectMapper().writeValueAsString(logOut));
    }

    @AfterReturning(pointcut = "pointcutRequest()", returning = "retorno")
    public void afterReturningResponseSuccess(final JoinPoint joinPoint, final Object retorno) {
//        final HttpServletResponse response = ((ServletRequestAttributes)
//                RequestContextHolder.getRequestAttributes()).getResponse();
//        Log logOut = mapReqResp.get(this.getTraceId());
//        logOut.getResponse().setHttpStatus(String.valueOf(response.getStatus()));
//        Map<String, String> headers = response.getHeaderNames().stream()
//                .collect(Collectors.toMap(h -> h, h -> response.getHeader(h)));
//        logOut.getResponse().setHeaders(headers);
//        logOut.getResponse().setBody(retorno);
//        logOut.setLevel(response.getStatus() > 199 && response.getStatus() < 300 ? "SUCCESS" : "FAILED");
//        logOut.setTraceId(this.getTraceId());
//        System.out.println(objectMapper().writeValueAsString(logOut));

        if (isNotServerResponseSuccess(joinPoint)) {
            //ServerHttpResponse response = ((ServerRequest) joinPoint.getArgs()[0]).exchange().getResponse().;
            //DataBuffer dataBuffer = response.bufferFactory().allocateBuffer();
        }


    }

    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }

    private boolean isNotServerResponseSuccess(JoinPoint joinPoint) {
        return !(joinPoint.getArgs() != null &&
                joinPoint.getArgs().length > 0 &&
                joinPoint.getArgs()[0] instanceof ServerRequest &&
                ((ServerRequest)joinPoint.getArgs()[0]).exchange().getResponse().getStatusCode().value() > 199 &&
                ((ServerRequest)joinPoint.getArgs()[0]).exchange().getResponse().getStatusCode().value() < 299);
    }

    private boolean isNotServerRequest(JoinPoint joinPoint) {
        return !(joinPoint.getArgs() != null && joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof ServerRequest);
    }

    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }

    private String getTraceId() {
        return UUID.randomUUID().toString();
    }

}