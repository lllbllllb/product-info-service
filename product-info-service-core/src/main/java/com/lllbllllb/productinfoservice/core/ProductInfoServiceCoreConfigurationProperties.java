package com.lllbllllb.productinfoservice.core;

import java.time.Duration;
import java.time.Period;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

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


    @Pattern(regexp = "^.+(\\.json)$", message = "Only JSON files supported")
    private String targetFileName = "product-info.json";

    @NotBlank
    private String pathToSaveTmp;

    @NotNull
    private CleanupPolicy cleanupPolicy = CleanupPolicy.ALL;

    @NotNull
    private Period reportPeriod = Period.ofYears(1);

    @NotNull
    private RetryOptions retryOptions = new RetryOptions();

    @NotNull
    private Duration concurrentCourtesyPeriod = Duration.ofMillis(20);

    @Data
    public static class RetryOptions {

        @PositiveOrZero
        private long maxAttempts = 4;

        @NotNull
        private Duration minBackoff = Duration.ofSeconds(8);
    }

}
