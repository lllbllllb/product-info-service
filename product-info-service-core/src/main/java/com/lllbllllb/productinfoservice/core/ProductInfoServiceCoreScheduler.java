package com.lllbllllb.productinfoservice.core;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@ConditionalOnProperty(prefix = "product-info-service.core", name = "service-mode", havingValue = "STANDALONE")
@RequiredArgsConstructor
public class ProductInfoServiceCoreScheduler {

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final ProductInfoServiceCoreEtlPipelineService mainFlowService;

    @PostConstruct
    public void init() {
        Flux.interval(properties.getDelay(), properties.getRefreshInterval())
            .flatMap(t -> mainFlowService.collect())
            .subscribe();
    }
}
