package com.lllbllllb.productinfoservice.controller;

import java.time.Duration;

import javax.validation.constraints.NotNull;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
@ConfigurationProperties("product-info-service.controller")
public class ProductInfoServiceControllerConfigurationProperties {

    @NotNull
    private Duration roundInterval = Duration.ofMinutes(1);

    @NotNull
    private Duration roundDelay = Duration.ofHours(1);

    private boolean localScheduled = true;
}
