package com.lllbllllb.productinfoservice.controller;

import javax.annotation.PostConstruct;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreScheduler {

    private final ProductInfoServiceCoreApiService apiService;

    private final ProductInfoServiceControllerConfigurationProperties properties;

    @PostConstruct
    public void init() {
        Flux.interval(properties.getRoundDelay(), properties.getRoundInterval())
            .flatMap(t -> apiService.refreshAll())
            .subscribe();
    }
}
