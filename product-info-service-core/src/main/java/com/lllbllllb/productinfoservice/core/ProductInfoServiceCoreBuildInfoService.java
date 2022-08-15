package com.lllbllllb.productinfoservice.core;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public Flux<BuildInfo> filterBuildInfosToProceed(List<BuildInfo> buildInfos) {
        return buildInfoRepositoryService.findAllBuildInfo(buildInfos)
            .collect(Collectors.toSet())
            .flatMapMany(buildInfoAwareSet -> {
                var keyToBuildInfoMap = buildInfoAwareSet.stream()
                    .collect(Collectors.toMap(bia -> getUniqueKey(bia.buildInfo()), Function.identity()));
                var buildInfosToUpdateSteam = buildInfos.stream()
                    .filter(buildInfo -> {
                        var key = getUniqueKey(buildInfo);
                        var newChecksum = buildInfo.checksum();

                        return !keyToBuildInfoMap.containsKey(key)
                            || Status.isFailed(keyToBuildInfoMap.get(key).obj())
                            || keyToBuildInfoMap.get(key).obj() == Status.FINISHED
                            && !checksumService.isChecksumTheSame(keyToBuildInfoMap.get(key).buildInfo().checksum(), newChecksum);
                    });

                return Flux.fromStream(buildInfosToUpdateSteam);
            });
    }

    public Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Round round) {
        return saveBuildInfo(buildInfo, round, Status.IN_PROGRESS);
    }

    public Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Round round, Status status) {
        return buildInfoRepositoryService.saveBuildInfo(buildInfo, round, status);
    }

    private String getUniqueKey(BuildInfo buildInfo) {
        var metadata = buildInfo.buildMetadata();

        return String.format("%s-%s", metadata.productCode(), metadata.fullNumber());
    }
}
