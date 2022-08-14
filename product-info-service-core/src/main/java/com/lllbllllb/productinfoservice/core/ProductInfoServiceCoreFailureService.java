package com.lllbllllb.productinfoservice.core;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFailureService {

    private final ProductInfoServiceCoreFinalizeService finalizeService;

    public <T> Mono<T> onErrorResume(Throwable ex, BuildInfo buildInfo, Status status, Mono<T> publisher) {
        return finalizeService.finalize(buildInfo, status)
            .doOnNext(bia -> log.error("{} = {}", bia, ex))
            .flatMap(bool -> publisher);
    }

    public <T> Flux<T> onErrorResume(Throwable ex, BuildInfo buildInfo, Status status, Flux<T> publisher) {
        return finalizeService.finalize(buildInfo, status)
            .doOnNext(bia -> log.error("{} = {}", bia, ex))
            .flatMapMany(bool -> publisher);
    }
}
