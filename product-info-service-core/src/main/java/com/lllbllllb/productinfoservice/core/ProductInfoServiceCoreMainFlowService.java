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

    private final ProductInfoServiceCoreFileService fileService;

    private final ProductInfoServiceCoreTarGzService tarGzService;

    private final ProductInfoServiceCoreBuildInfoService buildInfoService;

    private final ProductInfoServiceCoreChecksumService checksumService;

    private final ProductInfoServiceRepositoryService repositoryService;

    private final ProductInfoServiceCoreFailureService failureService;


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
        checksumService.filterUnchangedByChecksums(buildInfos)
            .log("2 BuildInfos to update ")
            .flatMap(buildInfo -> repositoryService.saveBuildInfo(buildInfo, Status.IN_PROGRESS))
            .flatMap(buildInfoAware -> buildDownloadService.downloadBuild(buildInfoAware.buildInfo()))
            .log("3 Build downloaded ")
            .flatMap(fileService::writeToFile)
            .log("4 Write to file ")
            .flatMap(checksumService::validateFileChecksum)
            .log("4.1 SHA256 OK ")
            .flatMap(tarGzService::extractFileFromPath)
            .log("5 File extracted ", Level.FINE)
            .flatMap(repositoryService::saveProductInfo)
//            .flatMap(buildInfoAware -> fileService.deleteFile(buildInfoAware.buildInfo())) fixme: use it NOW
            .log("6 Tidied up ")
            .onErrorContinue(failureService::handleFail)
            .subscribe();
    }
}
