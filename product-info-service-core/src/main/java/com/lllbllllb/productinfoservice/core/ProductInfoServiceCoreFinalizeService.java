package com.lllbllllb.productinfoservice.core;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryLocalService;
import com.lllbllllb.productinfoservice.core.model.CleanupPolicy;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFinalizeService {

    private final ProductInfoServiceRepositoryLocalService repositoryService;

    private final ProductInfoServiceCoreFileService fileService;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    public Mono<BuildInfoAware<Boolean>> finalize(BuildInfo buildInfo) {
        return finalize(buildInfo, Status.FINISHED);
    }

    public Mono<BuildInfoAware<Boolean>> finalize(BuildInfo buildInfo, Status status) {
        return repositoryService.saveBuildInfo(buildInfo, status)
            .flatMap(buildInfoAware -> {
                if (CleanupPolicy.ALL == properties.getCleanupPolicy()) {
                    return fileService.deleteFile(buildInfo)
                        .map(deleted -> new BuildInfoAware<>(buildInfo, deleted));
                }

                return Mono.just(new BuildInfoAware<>(buildInfo, true));
            });
    }
}
