package com.lllbllllb.productinfoservice.core;

import java.util.List;
import java.util.logging.Level;

import javax.annotation.PreDestroy;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryRemoteService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreEtlPipelineService {

    private final ProductInfoServiceCoreBuildDownloadService buildDownloadService;

    private final ProductInfoServiceCoreFileCacheService fileCacheService;

    private final ProductInfoServiceCoreFileExtractionService fileExtractionService;

    private final ProductInfoServiceCoreBuildInfoService buildInfoService;

    private final ProductInfoServiceCoreChecksumService checksumService;

    private final ProductInfoServiceCoreProductInfoService productInfoService;

    private final ProductInfoServiceRepositoryRemoteService repositoryRemoteService;

    private final ProductInfoServiceCoreFinalizeService finalizeService;

    private final ProductInfoServiceCoreRoundService roundService;

    @PreDestroy
    public void onDestroy() {
    }

    public Flux<BuildInfoAware<Round>> startRound() {
        return process(repositoryRemoteService.getAllBuildInfo());
    }

    public Flux<BuildInfoAware<Round>> startRound(String productCode) {
        return process(repositoryRemoteService.getBuildInfoByProductCode(productCode));
    }

    private Flux<BuildInfoAware<Round>> process(Flux<BuildInfo> stream) {
        return stream.collectList()
            .log(this.getClass().getName(), Level.FINE)
            .zipWith(roundService.createRound()
                .log(this.getClass().getName()))
            .doOnNext(tuple2 -> fireAndForget(tuple2.getT1(), tuple2.getT2()))
            .flatMapMany(tuple2 -> Flux.fromIterable(tuple2.getT1())
                .map(buildInfo -> new BuildInfoAware<>(buildInfo, tuple2.getT2())));
    }

    private void fireAndForget(List<BuildInfo> buildInfos, Round round) {
        buildInfoService.filterBuildInfosToProceed(buildInfos)
            .log(this.getClass().getName(), Level.FINE)
            .flatMap(buildInfo -> buildInfoService.saveBuildInfo(buildInfo, round))
            .flatMap(buildInfoAware -> buildDownloadService.downloadBuild(buildInfoAware.buildInfo(), round))
            .flatMap(buildInfoAware -> fileCacheService.writeToFile(buildInfoAware.buildInfo(), buildInfoAware.obj(), round))
            .flatMap(buildInfoAware -> checksumService.validateFileChecksum(buildInfoAware.buildInfo(), buildInfoAware.obj(), round))
            .flatMap(buildInfoAware -> fileExtractionService.extractFileFromPath(buildInfoAware.buildInfo(), buildInfoAware.obj(), round))
            .flatMap(buildInfoAware -> productInfoService.saveProductInfo(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> finalizeService.finalize(buildInfoAware.buildInfo(), round))
            .subscribe();
    }
}
