package com.lllbllllb.productinfoservice.repositoryremote;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryRemoteService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceRepositoryRemoteServiceImpl implements ProductInfoServiceRepositoryRemoteService {

    private static final int SHA_256_LENGTH = 64;
    private final WebClient releasesCodeClient;

    private final WebClient commonGuestClient;

    private final ProductInfoServiceCoreBuildsMetadataService buildsMetadataService;

    private final ProductInfoServiceRepositoryRemoteConfigurationProperties properties;

    @Override
    public Flux<BuildInfo> getAllBuildInfo() {
        return process(buildsMetadataService.getAllBuildsMetadata());
    }

    @Override
    public Flux<BuildInfo> getBuildInfoByProductCode(String productCode) {
        return process(buildsMetadataService.getBuildsMetadataByProduct(productCode));
    }

    private Flux<BuildInfo> process(Mono<Collection<Map.Entry<String, Set<BuildMetadata>>>> pub) {
        return pub
            .flatMapMany(Flux::fromIterable)
            .flatMap(entry -> getBuildInfos(entry.getKey(), entry.getValue()));
    }

    private Flux<BuildInfo> getBuildInfos(String productCode, Collection<BuildMetadata> buildMetadata) {
        var buildNumberToBuildMetadataMap = buildMetadata.stream()
            .collect(Collectors.toMap(BuildMetadata::fullNumber, Function.identity()));

        return releasesCodeClient.get()
            .uri(uriBuilder -> uriBuilder.queryParam("code", productCode).build())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .flatMapMany(release -> getBuildInfos(release, buildNumberToBuildMetadataMap));
    }

    private Flux<BuildInfo> getBuildInfos(JsonNode releases, Map<String, BuildMetadata> buildNumberToBuildMetadataMap) {
        return Flux.fromStream(parseProductReleases(releases, buildNumberToBuildMetadataMap))
            .flatMap(Function.identity());
    }

    private Stream<Mono<BuildInfo>> parseProductReleases(JsonNode releases, Map<String, BuildMetadata> buildNumberToBuildMetadataMap) {
        var fieldNames = releases.fieldNames();

        if (fieldNames.hasNext()) {
            var productCode = fieldNames.next();

            return StreamSupport
                .stream(releases.get(productCode).spliterator(), false)
                .filter(child -> buildNumberToBuildMetadataMap.containsKey(child.get("build").asText())
                    && child.get("downloads").get(properties.getLinuxDistroKey()) != null)
                .map(child -> parseChildJsonNode(child, buildNumberToBuildMetadataMap));
        } else {
            return Stream.empty();
        }
    }

    private Mono<BuildInfo> parseChildJsonNode(JsonNode child, Map<String, BuildMetadata> buildNumberToBuildMetadataMap) {
        var build = child.get("build").asText();
        var linuxDownload = child.get("downloads").get(properties.getLinuxDistroKey());
        var checksumLink = linuxDownload.get("checksumLink").textValue();

        return getExpectedChecksum(checksumLink)
            .map(checksum -> new BuildInfo(
                linuxDownload.get("link").textValue(),
                linuxDownload.get("size").longValue(),
                linuxDownload.get("checksumLink").textValue(),
                buildNumberToBuildMetadataMap.get(build),
                checksum
            ));
    }

    private Mono<String> getExpectedChecksum(String checksumLink) {
        return commonGuestClient.get()
            .uri(URI.create(checksumLink))
            .accept(MediaType.TEXT_HTML)
            .retrieve()
            .bodyToMono(String.class)
            .map(this::sanitizeChecksum);
    }

    private String sanitizeChecksum(String source) {
        return source.substring(0, SHA_256_LENGTH).toLowerCase();
    }
}
