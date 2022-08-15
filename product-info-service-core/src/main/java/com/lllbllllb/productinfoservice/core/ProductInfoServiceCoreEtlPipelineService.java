package com.lllbllllb.productinfoservice.core;

import java.util.List;
import java.util.logging.Level;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryRemoteService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreEtlPipelineService {

    private final ProductInfoServiceCoreBuildDownloadService buildDownloadService;

    private final ProductInfoServiceCoreFileCacheService fileCacheService;

    private final ProductInfoServiceCoreTarGzService tarGzService;

    private final ProductInfoServiceCoreBuildInfoService buildInfoService;

    private final ProductInfoServiceCoreChecksumService checksumService;

    private final ProductInfoServiceCoreProductInfoService productInfoService;

    private final ProductInfoServiceRepositoryRemoteService repositoryRemoteService;

    private final ProductInfoServiceCoreFinalizeService finalizeService;

    private final ProductInfoServiceCoreRoundService roundService;

    public Mono<List<BuildInfo>> collect() {
        return process(repositoryRemoteService.getAllBuildInfo());
    }

    public Mono<List<BuildInfo>> collect(String productCode) {
        return process(repositoryRemoteService.getBuildInfoByProductCode(productCode));
    }

    private Mono<List<BuildInfo>> process(Flux<BuildInfo> stream) {
        return stream.collectList()
            .log(this.getClass().getName(), Level.FINE)
            .zipWith(roundService.createRound())
            .doOnNext(tuple2 -> fireAndForget(tuple2.getT1(), tuple2.getT2()))
            .map(Tuple2::getT1);
    }

    private void fireAndForget(List<BuildInfo> buildInfos, Round round) {
        buildInfoService.filterBuildInfosToProceed(buildInfos)
            .log(this.getClass().getName(), Level.FINE)
            .flatMap(buildInfo -> buildInfoService.saveBuildInfo(buildInfo, round))
            .flatMap(buildInfoAware -> buildDownloadService.downloadBuild(buildInfoAware.buildInfo()))
            .flatMap(buildInfoAware -> fileCacheService.writeToFile(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> checksumService.validateFileChecksum(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> tarGzService.extractFileFromPath(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> productInfoService.saveProductInfo(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> finalizeService.finalize(buildInfoAware.buildInfo(), round))
            .subscribe();
    }
}
