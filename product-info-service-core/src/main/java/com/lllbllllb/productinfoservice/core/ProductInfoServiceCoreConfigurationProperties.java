package com.lllbllllb.productinfoservice.core;

import java.time.Duration;
import java.time.Period;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.lllbllllb.productinfoservice.core.model.CleanupPolicy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Component
@ConfigurationProperties("product-info-service.core")
public class ProductInfoServiceCoreConfigurationProperties {

    @NotNull
    private Duration refreshInterval = Duration.ofHours(1);

    @NotNull
    private Duration delay = Duration.ofHours(1);

    @Pattern(regexp = "^.+(\\.json)$", message = "Only JSON files supported")
    private String targetFileName = "product-info.json";

    @Pattern(regexp = "^(http|https)://.+(\\.xml)$", message = "Invalid http link")
    private String updatesXmlUrl = "https://www.jetbrains.com/updates/updates.xml";

    @Pattern(regexp = "^(http|https)://.+$", message = "Invalid http link")
    private String releasesCodeUrl = "https://data.services.jetbrains.com/products/releases";

    @NotBlank
    private String linuxDistroKey = "linux";

    @NotNull
    private Period buildMaxAge = Period.ofYears(1);

    @NotBlank
    private String pathToSaveTmp;

    @NotNull
    private CleanupPolicy cleanupPolicy = CleanupPolicy.ALL;

}
