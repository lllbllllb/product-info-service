package com.lllbllllb.productinfoservice.core;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.lllbllllb.productinfoservice.core.model.BuildMetadata;
import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildInfoProvider {

    private final WebClient releasesCodeClient;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    public Flux<BuildInfo> getBuildInfos(String code, Collection<BuildMetadata> buildMetadata) {
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
