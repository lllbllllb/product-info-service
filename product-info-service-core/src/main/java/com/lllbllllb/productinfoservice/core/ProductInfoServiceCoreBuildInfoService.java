package com.lllbllllb.productinfoservice.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import com.lllbllllb.productinfoservice.core.model.BuildMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildInfoService {

    private final ProductInfoServiceCoreBuildsMetadataService buildsMetadataService;

    private final ProductInfoServiceCoreBuildInfoProvider buildInfoProvider;

    public Flux<BuildInfo> getAllBuildInfo() {
        return process(buildsMetadataService.getAllBuildsMetadata());
    }

    public Flux<BuildInfo> getBuildInfoByProductCode(String productCode) {
        return process(buildsMetadataService.getBuildsMetadataByProduct(productCode));
    }

    private Flux<BuildInfo> process(Mono<Collection<Map.Entry<String, Set<BuildMetadata>>>> pub) {
        return pub
            .flatMapMany(Flux::fromIterable)
            .flatMap(entry -> buildInfoProvider.getBuildInfos(entry.getKey(), entry.getValue()));
    }
}
