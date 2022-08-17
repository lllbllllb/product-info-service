package com.lllbllllb.productinfoservice.repositoryremote;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.PostConstruct;

import com.lllbllllb.productinfoservice.model.BuildMetadata;
import com.lllbllllb.productinfoservice.repositoryremote.model.updatesxml.Build;
import com.lllbllllb.productinfoservice.repositoryremote.model.updatesxml.Channel;
import com.lllbllllb.productinfoservice.repositoryremote.model.updatesxml.Product;
import com.lllbllllb.productinfoservice.repositoryremote.model.updatesxml.Products;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildsMetadataService {

    private final WebClient updatesXmlClient;

    private final Clock clock;

    private final ProductInfoServiceRepositoryRemoteConfigurationProperties properties;

    @PostConstruct
    public void init() {
        lastCheck = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), clock.getZone());
    }

    private volatile ZonedDateTime lastCheck;

    public Mono<Collection<Map.Entry<String, Set<BuildMetadata>>>> getBuildsMetadataByProduct(String productCode) {
        return getProducts()
            .map(products -> {
                var codeToBuildNumbersMap = new HashMap<String, Set<BuildMetadata>>();
                var targetProduct = products.getProduct().stream()
                    .filter(product -> product.getCode().contains(productCode))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown product code [%s]", productCode)));
                var builds = extractBuildMetadata(targetProduct, productCode);

                codeToBuildNumbersMap.put(productCode, builds);

                return codeToBuildNumbersMap.entrySet();
            });
    }

    public Mono<Collection<Map.Entry<String, Set<BuildMetadata>>>> getAllBuildsMetadata() {
        return getProducts()
            .doOnNext(products -> fireLastCheck()) // fixme: move to end of the flow
            .map(products -> {
                var codeToBuildNumbersMap = new HashMap<String, Set<BuildMetadata>>();

                products.getProduct().forEach(product -> product.getCode().forEach(code -> {
                    var metadata = extractBuildMetadata(product, code);

                    codeToBuildNumbersMap.put(code, metadata);
                }));

                return codeToBuildNumbersMap.entrySet();
            });
    }

    public void fireLastCheck() {
        lastCheck = ZonedDateTime.now(clock);
    }

    private Mono<Products> getProducts() {
        return updatesXmlClient.get()
            .ifModifiedSince(lastCheck)
            .retrieve()
            .onStatus(HttpStatus::is3xxRedirection, clientResponse -> Mono.empty())
            .bodyToMono(Products.class)
            .log(this.getClass().getName(), Level.FINE);
    }

    private Set<BuildMetadata> extractBuildMetadata(Product product, String productCode) {
        var buildInfos = new HashSet<BuildMetadata>();

        for (Channel channel : product.getChannel()) {
            for (Build build : channel.getBuild()) {
                if (isValidBuild(build)) {
                    var buildInfo = new BuildMetadata(
                        product.getName(),
                        channel.getName(),
                        channel.getStatus(),
                        build.getVersion(),
                        build.getReleaseDate(),
                        build.getFullNumber(),
                        productCode
                    );

                    buildInfos.add(buildInfo);
                }
            }
        }

        return buildInfos;
    }

    private boolean isValidBuild(Build build) {
        if (build.getButton() == null || build.getReleaseDate() == null) {
            return false;
        }

        var bound = build.getReleaseDate().plus(properties.getBuildMaxAge());
        var now = LocalDate.now(clock);

        return bound.isAfter(now) || bound.isEqual(now);
    }
}
