package com.lllbllllb.productinfoservice.core;

import java.util.List;
import java.util.logging.Level;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreMainFlowService { // fixme: rename to ProductInfoServiceCorePipelineService

    private final ProductInfoServiceCoreBuildDownloadService buildDownloadService;

    private final ProductInfoServiceCoreFileCacheService fileCacheService;

    private final ProductInfoServiceCoreTarGzService tarGzService;

    private final ProductInfoServiceCoreBuildInfoService buildInfoService;

    private final ProductInfoServiceCoreChecksumService checksumService;

    private final ProductInfoServiceRepositoryService repositoryService;

    private final ProductInfoServiceCoreFinalizeService finalizeService;

    public Mono<List<BuildInfo>> collect() {
        return process(buildInfoService.getAllBuildInfo()); // fixme: cheat and tricky
    }

    public Mono<List<BuildInfo>> collect(String productCode) {
        return process(buildInfoService.getBuildInfoByProductCode(productCode));
    }

    private Mono<List<BuildInfo>> process(Flux<BuildInfo> stream) {
        return stream.collectList()
            .log("1 Accepted BuildInfos ")
            .doOnNext(this::fireAndForget);
    }

    private void fireAndForget(List<BuildInfo> buildInfos) {
        buildInfoService.filterBuildInfosToProceed(buildInfos)
            .log("2 BuildInfos to update ")
            .flatMap(buildInfo -> repositoryService.saveBuildInfo(buildInfo, Status.IN_PROGRESS))
            .flatMap(buildInfoAware -> buildDownloadService.downloadBuild(buildInfoAware.buildInfo()))
            .log("3 Build stream accepted ")
            .flatMap(buildInfoAware -> fileCacheService.writeToFile(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .log("4 Write to file ")
            .flatMap(buildInfoAware -> checksumService.validateFileChecksum(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .log("4.1 SHA256 OK ")
            .flatMap(buildInfoAware -> tarGzService.extractFileFromPath(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .log("5 File extracted ", Level.FINE)
            .flatMap(buildInfoAware -> repositoryService.saveProductInfo(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> finalizeService.finalize(buildInfoAware.buildInfo()))
            .log("6 Finalized ")
            .subscribe();
    }
}
