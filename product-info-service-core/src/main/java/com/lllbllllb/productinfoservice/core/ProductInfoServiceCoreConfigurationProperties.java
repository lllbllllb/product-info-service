package com.lllbllllb.productinfoservice.core;

import java.time.Duration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("product-info-service.core")
public class ProductInfoServiceCoreConfigurationProperties {

    private Duration refreshInterval = Duration.ofHours(1);

    private String targetFileName = "product-info.json";

    private String buildsListUrl = "https://www.jetbrains.com/updates/updates.xml";


}
