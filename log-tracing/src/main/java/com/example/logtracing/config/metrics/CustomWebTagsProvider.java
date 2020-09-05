package com.example.logtracing.config.metrics;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.boot.actuate.metrics.web.servlet.DefaultWebMvcTagsProvider;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class CustomWebTagsProvider extends DefaultWebMvcTagsProvider {

    private static final String TAG_CHANNEL = "channel";

    @Override
    public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable exception) {
        Tags tags = Tags.of(super.getTags(request, response, handler, exception));
        String channel = request.getHeader("CHANNEL");
        return tags.and(TAG_CHANNEL, Objects.isNull(channel) ? "None" : channel );
    }
}
