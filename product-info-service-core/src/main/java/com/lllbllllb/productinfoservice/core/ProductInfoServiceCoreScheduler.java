package com.lllbllllb.productinfoservice.core;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreScheduler {

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final ProductInfoServiceCoreProductInfoDataCollector productInfoDataCollector;

    @PostConstruct
    public void init() {
        Flux.interval(properties.getRefreshInterval())
//            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(t -> productInfoDataCollector.collect());
    }
}
