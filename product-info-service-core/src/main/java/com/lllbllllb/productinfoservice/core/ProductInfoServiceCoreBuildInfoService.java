package com.lllbllllb.productinfoservice.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    private final ProductInfoServiceCoreChecksumService checksumService;

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
            .flatMapMany(release -> getBuildInfos(release, code, buildNumberToBuildMetadataMap));
    }

    private Flux<BuildInfo> getBuildInfos(JsonNode releases, String code, Map<String, BuildMetadata> buildNumberToBuildMetadataMap) {
        return Flux.fromStream(parseProductReleases(releases, code, buildNumberToBuildMetadataMap))
            .flatMap(Function.identity());
    }

    private Stream<Mono<BuildInfo>> parseProductReleases(JsonNode releases, String code, Map<String, BuildMetadata> buildNumberToBuildMetadataMap) {
        var fieldNames = releases.fieldNames(); // expected just one, e.g. IC, IU, WS, CL, etc.

        if (fieldNames.hasNext()) {
            var productCode = fieldNames.next();

            return StreamSupport
                .stream(releases.get(productCode).spliterator(), false)
                .filter(child -> buildNumberToBuildMetadataMap.containsKey(child.get("build").asText())
                    && child.get("downloads").get(properties.getLinuxDistroKey()) != null)
                .map(child -> parseChildJsonNode(child, code, buildNumberToBuildMetadataMap));
        } else {
            return Stream.empty();
        }
    }

    private Mono<BuildInfo> parseChildJsonNode(JsonNode child, String code, Map<String, BuildMetadata> buildNumberToBuildMetadataMap) {
        var build = child.get("build").asText();
        var linuxDownload = child.get("downloads").get(properties.getLinuxDistroKey());
        var checksumLink = linuxDownload.get("checksumLink").textValue();

        return checksumService.getExpectedChecksum(checksumLink)
            .map(checksum -> new BuildInfo(
                linuxDownload.get("link").textValue(),
                linuxDownload.get("size").longValue(),
                linuxDownload.get("checksumLink").textValue(),
                buildNumberToBuildMetadataMap.get(build),
                code,
                checksum
            ));
    }
}
