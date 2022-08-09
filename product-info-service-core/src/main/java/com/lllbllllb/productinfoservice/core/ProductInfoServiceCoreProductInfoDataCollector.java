package com.lllbllllb.productinfoservice.core;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.lllbllllb.productinfoservice.core.model.BuildMetadata;
import com.lllbllllb.productinfoservice.core.model.updatesxml.Build;
import com.lllbllllb.productinfoservice.core.model.updatesxml.Channel;
import com.lllbllllb.productinfoservice.core.model.updatesxml.Product;
import com.lllbllllb.productinfoservice.core.model.updatesxml.Products;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreProductInfoDataCollector {

    private final WebClient updatesXmlClient;

    private final Clock clock;

    private final ProductInfoServiceCoreBuildInfoProvider downloadInfoProvider;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final ProductInfoServiceCoreBuildDownloadService buildDownloadService;

    private volatile ZonedDateTime lastCheck;


    @PostConstruct
    public void init() {
        lastCheck = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, clock.getZone());
    }

    public void collect() {
        collect(null);
    }

    public void collect(String productCode) {
        updatesXmlClient.get()
//            .ifModifiedSince(lastCheck)
            .retrieve()
            .onStatus(HttpStatus::is3xxRedirection, clientResponse -> Mono.empty())
            .bodyToMono(Products.class)
            .doOnNext(products -> lastCheck = ZonedDateTime.now(clock))
            .subscribe(products -> {
                var codeToBuildNumbersMap = new HashMap<String, Set<BuildMetadata>>();

                if (productCode != null) {
                    var targetProduct = products.getProduct().stream()
                        .filter(product -> product.getCode().contains(productCode))
                        .findAny()
                        .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown product code [%s]", productCode)));
                    var builds = extractBuildInfos(targetProduct);

                    codeToBuildNumbersMap.put(productCode, builds);
                } else {
                    products.getProduct().forEach(product -> {
                        var builds = extractBuildInfos(product);

                        product.getCode().forEach(code -> codeToBuildNumbersMap.put(code, builds));
                    });

                }

                codeToBuildNumbersMap.forEach((code, builds) -> downloadInfoProvider.getBuildInfos(code, builds)
                    .subscribe(buildDownloadService::downloadBuild));
            });
    }

    private Set<BuildMetadata> extractBuildInfos(Product product) {

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
                        build.getFullNumber()
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
