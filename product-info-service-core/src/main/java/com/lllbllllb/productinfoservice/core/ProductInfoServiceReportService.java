package com.lllbllllb.productinfoservice.core;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.lllbllllb.productinfoservice.ProductInfoServiceBuildInfoRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceReportService {

    private final ProductInfoServiceBuildInfoRepositoryService buildInfoRepositoryService;

    private final Clock clock;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    public Mono<Collection<BuildInfoAware<Round>>> getLastReleasedBuildInfos() {
        var period = properties.getReportPeriod();
        var now = clock.instant();
        var from = ZonedDateTime.now(clock).minus(period).toInstant();

        return buildInfoRepositoryService.findAllFinishedBuildsByPeriod(from, now)
            .collectList()
            .map(builds -> builds.stream()
                .collect(Collectors.toMap(
                    bia -> bia.buildInfo().buildMetadata().productCode(),
                    Function.identity(),
                    this::getLatestBuild
                ))
                .values());
    }

    public Flux<BuildInfoAware<Pair<Status, Round>>> getActiveRoundData() {
        return buildInfoRepositoryService.findAllFromActiveRounds();
    }

    private BuildInfoAware<Round> getLatestBuild(BuildInfoAware<Round> first, BuildInfoAware<Round> second) {
        if (first.obj().cratedDate().equals(second.obj().cratedDate())) {
            if (first.buildInfo().buildMetadata().releaseDate().isAfter(second.buildInfo().buildMetadata().releaseDate())) {
                return first;
            } else {
                return second;
            }
        } else {
            if (first.obj().cratedDate().isAfter(second.obj().cratedDate())) {
                return first;
            } else {
                return second;
            }
        }
    }
}
