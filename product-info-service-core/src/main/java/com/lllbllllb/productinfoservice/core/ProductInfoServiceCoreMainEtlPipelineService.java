package com.lllbllllb.productinfoservice.core;

import java.util.List;
import java.util.logging.Level;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryLocalService;
import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryRemoteService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreMainEtlPipelineService {

    private final ProductInfoServiceCoreBuildDownloadService buildDownloadService;

    private final ProductInfoServiceCoreFileCacheService fileCacheService;

    private final ProductInfoServiceCoreTarGzService tarGzService;

    private final ProductInfoServiceCoreBuildInfoService buildInfoService;

    private final ProductInfoServiceCoreChecksumService checksumService;

    private final ProductInfoServiceRepositoryLocalService localRepositoryService;

    private final ProductInfoServiceRepositoryRemoteService repositoryRemoteService;

    private final ProductInfoServiceCoreFinalizeService finalizeService;

    public Mono<List<BuildInfo>> collect() {
        return process(repositoryRemoteService.getAllBuildInfo());
    }

    public Mono<List<BuildInfo>> collect(String productCode) {
        return process(repositoryRemoteService.getBuildInfoByProductCode(productCode));
    }

    private Mono<List<BuildInfo>> process(Flux<BuildInfo> stream) {
        return stream.collectList()
            .log(this.getClass().getName(), Level.FINE)
            .doOnNext(this::fireAndForget);
    }

    private void fireAndForget(List<BuildInfo> buildInfos) {
        buildInfoService.filterBuildInfosToProceed(buildInfos)
            .log(this.getClass().getName(), Level.FINE)
            .flatMap(buildInfo -> localRepositoryService.saveBuildInfo(buildInfo, Status.IN_PROGRESS))
            .flatMap(buildInfoAware -> buildDownloadService.downloadBuild(buildInfoAware.buildInfo()))
            .flatMap(buildInfoAware -> fileCacheService.writeToFile(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> checksumService.validateFileChecksum(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> tarGzService.extractFileFromPath(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> localRepositoryService.saveProductInfo(buildInfoAware.buildInfo(), buildInfoAware.obj()))
            .flatMap(buildInfoAware -> finalizeService.finalize(buildInfoAware.buildInfo()))
            .subscribe();
    }
}
