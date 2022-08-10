package com.lllbllllb.productinfoservice.core;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import com.lllbllllb.productinfoservice.core.model.BuildMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildInfoService {

    private final ProductInfoServiceCoreBuildsMetadataService buildsMetadataService;

    private final WebClient releasesCodeClient;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    public Flux<BuildInfo> getAllBuildInfo() {
        return process(buildsMetadataService.getAllBuildsMetadata());
    }

    public Flux<BuildInfo> getBuildInfoByProductCode(String productCode) {
        return process(buildsMetadataService.getBuildsMetadataByProduct(productCode));
    }

    private Flux<BuildInfo> process(Mono<Collection<Map.Entry<String, Set<BuildMetadata>>>> pub) {
        return pub
            .flatMapMany(Flux::fromIterable)
            .flatMap(entry -> getBuildInfos(entry.getKey(), entry.getValue()));
    }

    private Flux<BuildInfo> getBuildInfos(String code, Collection<BuildMetadata> buildMetadata) {
        var buildNumberToBuildMetadataMap = buildMetadata.stream()
            .collect(Collectors.toMap(BuildMetadata::fullNumber, Function.identity()));

        return releasesCodeClient.get()
            .uri(uriBuilder -> uriBuilder.queryParam("code", code).build())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(node -> {
                var key = node.fieldNames().next(); // so yes?

                return StreamSupport
                    .stream(node.get(key).spliterator(), false)
                    .filter(child -> buildNumberToBuildMetadataMap.containsKey(child.get("build").asText()))
                    .map(child -> {
                        var build = child.get("build").asText();

                        if (!buildNumberToBuildMetadataMap.containsKey(build)) {
                            return null;
                        }

                        var linuxDownload = child.get("downloads").get(properties.getLinuxDistroKey());

                        if (linuxDownload == null) {
                            return null;
                        }

                        return new BuildInfo(
                            linuxDownload.get("link").textValue(),
                            linuxDownload.get("size").longValue(),
                            linuxDownload.get("checksumLink").textValue(),
                            buildNumberToBuildMetadataMap.get(build)
                        );
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            })
            .flatMapMany(Flux::fromIterable);
    }
}
