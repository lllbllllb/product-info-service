package com.lllbllllb.productinfoservice.core;

import java.net.URI;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildDownloadService {

    private final WebClient redirectedWebClient;

    private final ProductInfoServiceCoreFileService fileService;

    private final ProductInfoServiceCoreFailureService failureService;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    @SneakyThrows
    public Mono<BuildInfoAware<Publisher<DataBuffer>>> downloadBuild(BuildInfo buildInfo) {
        var stream = fileService.getFileSize(buildInfo)
            .flatMapMany(actualSize -> {
                var delta = buildInfo.size() - actualSize;

                if (delta == 0) {
                    return Flux.empty();
                }

                var rangeValue = String.format("bytes=-%s", delta);

                return redirectedWebClient.get()
                    .uri(URI.create(buildInfo.link()))
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Range", rangeValue)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class);
            })
            .retryWhen(Retry.backoff(properties.getRetryOptions().getMaxAttempts(), properties.getRetryOptions().getMinBackoff()))
            .onErrorResume(ex -> failureService.onErrorResume(ex, buildInfo, Status.FAILED_DOWNLOAD, Flux.empty()));

        return Mono.just(new BuildInfoAware<>(buildInfo, stream));
    }


}
