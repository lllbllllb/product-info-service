package com.lllbllllb.productinfoservice.controller;

import java.time.Duration;

import javax.validation.constraints.NotNull;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Controller configuration properties. See {@code product-info-service.controller} path.
 */
@Data
@Validated
@Component
@ConfigurationProperties("product-info-service.controller")
public class ProductInfoServiceControllerConfigurationProperties {

    /**
     * Interval between rounds of the scan.
     */
    @NotNull
    private Duration roundInterval = Duration.ofHours(1);

    /**
     * Delay before first scan after bootstrap.
     */
    @NotNull
    private Duration roundDelay = Duration.ofMinutes(1);

    /**
     * If {@code true} local scheduler will be enabled.
     * {@code false} can be useful in case distributed system with single common scheduler.
     *
     * @see ProductInfoServiceControllerScheduler
     */
    private boolean localScheduled = true;
}
