package com.lllbllllb.productinfoservice.core;

import java.time.Duration;
import java.time.Period;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("product-info-service.core")
public class ProductInfoServiceCoreConfigurationProperties {

    private Duration refreshInterval = Duration.ofHours(1);

    private Duration delay = Duration.ofHours(1);

    private String targetFileName = "product-info.json";

    private String updatesXmlUrl = "https://www.jetbrains.com/updates/updates.xml";

    private String releasesCodeUrl = "https://data.services.jetbrains.com/products/releases";

    private ProductInfoServiceMode serviceMode = ProductInfoServiceMode.STANDALONE;

    private String linuxDistroKey = "linux";

    private Period buildMaxAge = Period.ofYears(1);

    private String pathToSave = "C:\\Users\\mrmbl\\Downloads\\testjb";

}
