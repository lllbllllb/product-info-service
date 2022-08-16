package com.lllbllllb.productinfoservice.core;

import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceBuildInfoRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildInfoService {

    private final ProductInfoServiceCoreChecksumService checksumService;

    private final ProductInfoServiceBuildInfoRepositoryService buildInfoRepositoryService;

    private final ProductInfoServiceCoreRandomService randomService;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    public Flux<BuildInfo> filterBuildInfosToProceed(List<BuildInfo> buildInfos) {
        var shuffled = randomService.shuffle(buildInfos);

        return Flux.fromIterable(shuffled)
            .delayElements(properties.getConcurrentCourtesyPeriod())
            .filterWhen(buildInfo -> buildInfoRepositoryService.findBuildInfo(buildInfo)
                .map(bia -> Status.isFailed(bia.obj()) || !checksumService.isChecksumTheSame(bia.buildInfo().checksum(), buildInfo.checksum()))
                .defaultIfEmpty(true));
    }

    public Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Round round) {
        return saveBuildInfo(buildInfo, round, Status.IN_PROGRESS);
    }

    public Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Round round, Status status) {
        return buildInfoRepositoryService.saveBuildInfo(buildInfo, round, status);
    }
}
