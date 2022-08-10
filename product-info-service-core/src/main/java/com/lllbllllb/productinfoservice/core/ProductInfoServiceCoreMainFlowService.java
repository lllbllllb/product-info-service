package com.lllbllllb.productinfoservice.core;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreMainFlowService {

    private final ProductInfoServiceCoreBuildDownloadService buildDownloadService;

    private final ProductInfoServiceCoreFileService fileService;

    private final ProductInfoServiceCoreTarGzService tarGzService;

    private final ProductInfoServiceCoreBuildInfoService buildInfoService;

    public Mono<List<BuildInfo>> collect() {
        return process(buildInfoService.getAllBuildInfo());
    }

    public Mono<List<BuildInfo>> collect(String productCode) {
        return process(buildInfoService.getBuildInfoByProductCode(productCode));
    }

    private Mono<List<BuildInfo>> process(Flux<BuildInfo> stream) {
        return stream.collectList()
            .log("1 BuildsMetadataByCode ")
            .doOnNext(this::fireAndForget);
    }

    private void fireAndForget(List<BuildInfo> buildInfos) {
        Flux.fromIterable(buildInfos)
            .log("2 BuildInfos ")
            .flatMap(buildDownloadService::downloadBuild)
            .log("3 Build downloaded ")
            .flatMap(pair -> fileService.writeToFile(pair.getFirst(), pair.getSecond()))
            .log("4 Write to file ")
            .flatMap(pair -> tarGzService.extractFileFromPath(pair.getRight())
                .map(file -> Pair.of(pair.getLeft(), file)))
            .log("5 File extracted ", Level.FINE)
            .map(pair -> new String(pair.getRight(), StandardCharsets.UTF_8))
            .log("6 product-info.json ")
            .then().subscribe();
    }
}
