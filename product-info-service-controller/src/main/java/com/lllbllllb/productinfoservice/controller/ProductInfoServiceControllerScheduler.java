package com.lllbllllb.productinfoservice.controller;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceControllerScheduler {

    private final ProductInfoServiceCoreApiService apiService;

    private final ProductInfoServiceControllerConfigurationProperties properties;

    /**
     * Local scheduler to run the scanning process.
     */
    @Scheduled(
        initialDelayString = "#{@productInfoServiceControllerConfigurationProperties.roundDelay}",
        fixedDelayString = "#{@productInfoServiceControllerConfigurationProperties.roundInterval}"
    )
    public void init() {
        if (properties.isLocalScheduled()) {
            apiService.refreshAll().subscribe();
        }
    }
}
