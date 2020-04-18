package com.example.cliente.config;

import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

import static feign.Util.UTF_8;

public class ApacheHttpClient implements Client {

    private static final String ACCEPT_HEADER_NAME = "Accept";
    private final HttpClient client;

    public ApacheHttpClient() {
        this(HttpClientBuilder.create().build());
    }

    public ApacheHttpClient(final HttpClient client) {
        this.client = client;
    }

    @Override
    public Response execute(final Request request, final Request.Options options) throws IOException {
        HttpUriRequest httpUriRequest;
        try {
            httpUriRequest = toHttpUriRequest(request, options);
        } catch (final URISyntaxException e) {
            throw new IOException("URL '" + request.url() + "' couldn't be parsed into a URI", e);
        }
        final HttpResponse httpResponse = client.execute(httpUriRequest);
        return toFeignResponse(httpResponse, request);
    }

    @SuppressWarnings("deprecation")
    HttpUriRequest toHttpUriRequest(final Request request, final Request.Options options) throws URISyntaxException {
        final RequestBuilder requestBuilder = RequestBuilder.create(request.httpMethod().name());
        final RequestConfig requestConfig = (client instanceof Configurable ? RequestConfig.copy(((Configurable) client).getConfig()) : RequestConfig.custom()).setConnectTimeout(options.connectTimeoutMillis()).setSocketTimeout(options.readTimeoutMillis()).build();
        requestBuilder.setConfig(requestConfig);
        final URI uri = new URIBuilder(request.url()).build();
        requestBuilder.setUri(uri.getScheme() + "://" + uri.getAuthority() + uri.getRawPath());
        final List<NameValuePair> queryParams = URLEncodedUtils.parse(uri, requestBuilder.getCharset().name());
        for (final NameValuePair queryParam : queryParams) {
            requestBuilder.addParameter(queryParam);
        }
        boolean hasAcceptHeader = false;
        for (final Map.Entry<String, Collection<String>> headerEntry : request.headers().entrySet()) {
            final String headerName = headerEntry.getKey();
            if (headerName.equalsIgnoreCase(ACCEPT_HEADER_NAME)) {
                hasAcceptHeader = true;
            }
            if (headerName.equalsIgnoreCase(Util.CONTENT_LENGTH)) {
                continue;
            }
            for (final String headerValue : headerEntry.getValue()) {
                requestBuilder.addHeader(headerName, headerValue);
            }
        }
        if (!hasAcceptHeader) {
            requestBuilder.addHeader(ACCEPT_HEADER_NAME, "*/*");
        }
        if (request.body() != null) {
            HttpEntity entity = null;
            if (request.charset() != null) {
                final ContentType contentType = getContentType(request);
                final String content = new String(request.body(), request.charset());
                entity = new StringEntity(content, contentType);
            } else {
                entity = new ByteArrayEntity(request.body());
            }
            requestBuilder.setEntity(entity);
        } else {
            requestBuilder.setEntity(new ByteArrayEntity(new byte[0]));
        }
        return requestBuilder.build();
    }

    @SuppressWarnings({ "static-method", "deprecation" })
    private ContentType getContentType(final Request request) {
        ContentType contentType = null;
        for (final Map.Entry<String, Collection<String>> entry : request.headers().entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Content-Type")) {
                final Collection<String> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    contentType = ContentType.parse(values.iterator().next());
                    if (contentType.getCharset() == null) {
                        contentType = contentType.withCharset(request.charset());
                    }
                    break;
                }
            }
        }
        return contentType;
    }

    Response toFeignResponse(final HttpResponse httpResponse, final Request request) {
        final StatusLine statusLine = httpResponse.getStatusLine();
        final int statusCode = statusLine.getStatusCode();
        final String reason = statusLine.getReasonPhrase();
        final Map<String, Collection<String>> headers = new HashMap<>();
        for (final Header header : httpResponse.getAllHeaders()) {
            final String name = header.getName();
            final String value = header.getValue();
            Collection<String> headerValues = headers.get(name);
            if (headerValues == null) {
                headerValues = new ArrayList<>();
                headers.put(name, headerValues);
            }
            headerValues.add(value);
        }
        return Response.builder().status(statusCode).reason(reason).headers(headers).request(request).body(toFeignBody(httpResponse)).build();
    }

    Response.Body toFeignBody(final HttpResponse httpResponse) {
        final HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return null;
        }
        return new Response.Body() {

            @Override
            public Integer length() {
                return entity.getContentLength() >= 0 &&
                        entity.getContentLength() <= Integer.MAX_VALUE ? (int) entity.getContentLength() : null;
            }

            @Override
            public boolean isRepeatable() {
                return entity.isRepeatable();
            }

            @Override
            public InputStream asInputStream() throws IOException {
                return entity.getContent();
            }

            @Override
            public Reader asReader() throws IOException {
                return new InputStreamReader(asInputStream(), UTF_8);
            }

            @Override
            public Reader asReader(final Charset charset) throws IOException {
                Util.checkNotNull(charset, "charset should not be null");
                return new InputStreamReader(asInputStream(), charset);
            }

            @Override
            public void close() throws IOException {
                EntityUtils.consume(entity);
            }
        };
    }
}
