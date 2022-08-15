package com.lllbllllb.productinfoservice.controller;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceControllerScheduler {

    private final ProductInfoServiceCoreApiService apiService;

    private final ProductInfoServiceControllerConfigurationProperties properties;

    public void init() {
        Flux.interval(properties.getRoundDelay(), properties.getRoundInterval())
            .concatMap(ignore -> apiService.refreshAll())
            .subscribe();
    }
}
