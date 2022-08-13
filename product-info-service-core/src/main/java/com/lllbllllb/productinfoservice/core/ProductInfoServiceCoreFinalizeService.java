package com.lllbllllb.productinfoservice.core;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryService;
import com.lllbllllb.productinfoservice.core.model.CleanupPolicy;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFinalizeService {

    private final ProductInfoServiceRepositoryService repositoryService;

    private final ProductInfoServiceCoreFileService fileService;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    public Mono<Boolean> finalize(BuildInfo buildInfo) {
        return finalize(buildInfo, Status.FINISHED);
    }

    public Mono<Boolean> finalize(BuildInfo buildInfo, Status status) {
        return repositoryService.saveBuildInfo(buildInfo, status)
            .flatMap(buildInfoAware -> {
                if (CleanupPolicy.ALL == properties.getCleanupPolicy()) {
                    return fileService.deleteFile(buildInfo);
                }

                return Mono.just(true);
            });
    }
}
