package com.lllbllllb.productinfoservice.core;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreScheduler {

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final ProductInfoServiceCoreMainFlowService mainFlowService;

    @PostConstruct
    public void init() {
        Flux.interval(properties.getDelay(), properties.getRefreshInterval())
            .flatMap(t -> mainFlowService.collect())
            .log("Build infos ")
            .subscribe();
    }
}
