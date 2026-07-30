package com.aiplatform.sentinel.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();

        filterChain.doFilter(requestWrapper, responseWrapper);

        long duration = System.currentTimeMillis() - start;

        String requestBody = new String(requestWrapper.getContentAsByteArray(),
                StandardCharsets.UTF_8);

        String responseBody = new String(responseWrapper.getContentAsByteArray(),
                StandardCharsets.UTF_8);

        log.info(
                """

                        ================ HTTP REQUEST ================
                        {} {}
                        Request Body:
                        {}

                        ================ HTTP RESPONSE ===============
                        Status : {}
                        Response Body:
                        {}

                        Execution Time : {} ms
                        ==============================================
                        """,
                request.getMethod(),
                request.getRequestURI(),
                requestBody,
                response.getStatus(),
                responseBody,
                duration);

        responseWrapper.copyBodyToResponse();
    }
}