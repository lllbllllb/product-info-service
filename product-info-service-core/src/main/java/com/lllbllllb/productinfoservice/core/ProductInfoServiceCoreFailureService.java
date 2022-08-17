package com.lllbllllb.productinfoservice.core;

import java.util.logging.Level;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFailureService {

    private final ProductInfoServiceCoreFinalizeService finalizeService;

    public <T> Mono<T> onErrorResume(Throwable ex, BuildInfo buildInfo, Status status, Mono<T> publisher) {
        return finalizeService.finalize(buildInfo, status)
            .log(this.getClass().getName(), Level.ALL, true, SignalType.ON_ERROR)
            .flatMap(bool -> publisher);
    }

    public <T> Flux<T> onErrorResume(Throwable ex, BuildInfo buildInfo, Status status, Flux<T> publisher) {
        return finalizeService.finalize(buildInfo, status)
            .log(this.getClass().getName(), Level.ALL, true, SignalType.ON_ERROR)
            .flatMapMany(bool -> publisher);
    }
}
