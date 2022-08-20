package com.lllbllllb.productinfoservice.repositoryremote;

import java.time.Period;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Component
@ConfigurationProperties("product-info-service.repository.remote")
public class ProductInfoServiceRepositoryRemoteConfigurationProperties {


    /**
     * Url to provider for metadata of the builds.
     */
    @Pattern(regexp = "^((http|https)://)?.+(\\.xml)$", message = "Invalid http link")
    private String updatesXmlUrl = "https://www.jetbrains.com/updates/updates.xml";

    /**
     * Url to provider for data of the builds.
     */
    @Pattern(regexp = "^((http|https)://)?.+$", message = "Invalid http link")
    private String releasesCodeUrl = "https://data.services.jetbrains.com/products/releases";

    /**
     * Name of the target platform.
     */
    @NotBlank
    private String targetPlatform = "linux";

    /**
     * Max age of the builds for processing.
     */
    @NotNull
    private Period buildMaxAge = Period.ofYears(1);
}
