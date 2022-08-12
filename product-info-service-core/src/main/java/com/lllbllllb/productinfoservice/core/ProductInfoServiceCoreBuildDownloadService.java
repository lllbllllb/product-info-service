package com.lllbllllb.productinfoservice.core;

import java.net.URI;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import com.lllbllllb.productinfoservice.core.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.core.model.ProgressStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildDownloadService {

    private final WebClient redirectedWebClient;

    private final ProductInfoServiceCoreProgressTrackerService progressTrackerService;

    private final ProductInfoServiceCoreFileService fileService;

    @SneakyThrows
    public Mono<BuildInfoAware<Flux<DataBuffer>>> downloadBuild(BuildInfo buildInfo) {
        var stream = fileService.getFileSize(buildInfo)
            .flatMapMany(actualSize -> {
                if (actualSize == buildInfo.size()) {
                    return Flux.empty();
                }

                var rangeValue = String.format("bytes=%s-", actualSize);

                return redirectedWebClient.get()
                    .uri(URI.create(buildInfo.link()))
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Range", rangeValue)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .doFinally(signal -> {
                        switch (signal) {
                            case CANCEL, ON_COMPLETE -> progressTrackerService.updateProgress(buildInfo, ProgressStatus.FINISHED);
                            case ON_ERROR -> progressTrackerService.updateProgress(buildInfo, ProgressStatus.FAILED);
                            default -> throw new RuntimeException(String.format("Download of the [%s] was aborted due to signal [%s]", buildInfo, signal));
                        }
                    });
            })
            .onErrorResume(ex -> Flux.empty());

        progressTrackerService.updateProgress(buildInfo, ProgressStatus.RUNNING);

        return Mono.just(new BuildInfoAware<>(buildInfo, stream));
    }


}
