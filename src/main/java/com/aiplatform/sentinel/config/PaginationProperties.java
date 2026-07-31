package com.aiplatform.sentinel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.pagination")
public class PaginationProperties {
    private int defaultPage;
    private int defaultSize;
    private String defaultSortBy;
    private String defaultSortDir;
}
