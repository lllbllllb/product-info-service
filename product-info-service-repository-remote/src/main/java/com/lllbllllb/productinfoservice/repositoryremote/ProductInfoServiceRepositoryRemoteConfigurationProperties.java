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


    @Pattern(regexp = "^((http|https)://)?.+(\\.xml)$", message = "Invalid http link")
    private String updatesXmlUrl = "https://www.jetbrains.com/updates/updates.xml";

    @Pattern(regexp = "^((http|https)://)?.+$", message = "Invalid http link")
    private String releasesCodeUrl = "https://data.services.jetbrains.com/products/releases";

    @NotBlank
    private String linuxDistroKey = "linux";

    @NotNull
    private Period buildMaxAge = Period.ofYears(1);
}
