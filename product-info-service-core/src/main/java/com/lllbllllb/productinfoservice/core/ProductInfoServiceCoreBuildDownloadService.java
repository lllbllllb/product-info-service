package com.lllbllllb.productinfoservice.core;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildDownloadService {

    private final WebClient redirectedWebClient;

    @Getter
    private final Map<BuildInfo, ProgressStatus> progressMap = new ConcurrentHashMap<>();

    @SneakyThrows
    public Mono<Pair<BuildInfo, Flux<DataBuffer>>> downloadBuild(BuildInfo buildInfo) {
        progressMap.put(buildInfo, ProgressStatus.RUNNING);

        var stream = redirectedWebClient.get()
            .uri(URI.create(buildInfo.link()))
            .accept(MediaType.APPLICATION_OCTET_STREAM)
//            .header("Range", String.format("bytes=%d-", buildInfo.size()))
            .retrieve()
            .bodyToFlux(DataBuffer.class)
            .doFinally(signal -> {
                switch (signal) {
                    case CANCEL, ON_COMPLETE -> progressMap.put(buildInfo, ProgressStatus.FINISHED);
                    case ON_ERROR -> progressMap.put(buildInfo, ProgressStatus.FAILED);
                    default -> throw new RuntimeException(String.format("Download of the [%s] was aborted due to signal [%s]", buildInfo, signal));
                }
            });

        return Mono.just(Pair.of(buildInfo, stream));
    }
}
