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

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildDownloadService {

    private final WebClient redirectedWebClient;

    private final ProductInfoServiceCoreFileService fileService;

    private final ProductInfoServiceCoreFailureService failureService;

    @SneakyThrows
    public Mono<BuildInfoAware<Publisher<DataBuffer>>> downloadBuild(BuildInfo buildInfo) {
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
                    .bodyToFlux(DataBuffer.class);
            })
            .onErrorResume(ex -> failureService.onErrorResume(buildInfo, Status.FAILED_DOWNLOAD, Flux.empty()));

        return Mono.just(new BuildInfoAware<>(buildInfo, stream));
    }


}
