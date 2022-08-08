package com.lllbllllb.productinfoservice.core;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreScheduler {

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final ProductInfoServiceCoreBuildsProcessor buildsProcessor;

    @PostConstruct
    public void init() {
        Flux.interval(properties.getRefreshInterval())
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(t -> buildsProcessor.process());
    }
}
