package com.lllbllllb.productinfoservice.core;

import com.lllbllllb.productinfoservice.ProductInfoServiceBuildInfoRepositoryService;
import com.lllbllllb.productinfoservice.core.model.CleanupPolicy;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFinalizeService {

    private final ProductInfoServiceCoreFileService fileService;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final ProductInfoServiceBuildInfoRepositoryService buildInfoRepositoryService;

    public Mono<BuildInfoAware<Boolean>> finalize(BuildInfo buildInfo, Round round) {
        return finalize(buildInfo, Status.FINISHED, round);
    }

    public Mono<BuildInfoAware<Boolean>> finalize(BuildInfo buildInfo, Status status, Round round) {
        return buildInfoRepositoryService.updateBuildInfo(buildInfo, status)
            .flatMap(bia -> {
                if (CleanupPolicy.ALL == properties.getCleanupPolicy()) {
                    return fileService.deleteFile(buildInfo, round)
                        .map(deleted -> new BuildInfoAware<>(buildInfo, deleted));
                }

                return Mono.just(new BuildInfoAware<>(buildInfo, true));
            });
    }
}
