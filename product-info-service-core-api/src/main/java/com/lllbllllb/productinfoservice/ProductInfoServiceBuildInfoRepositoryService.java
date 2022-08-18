package com.lllbllllb.productinfoservice;

import java.time.Instant;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * API for {@link BuildInfo} repository service.
 */
public interface ProductInfoServiceBuildInfoRepositoryService {

    /**
     * Save {@link BuildInfo}.
     *
     * @param buildInfo {@link BuildInfo} to save
     * @param round     current {@link Round}
     * @param status    {@link Status}
     * @return {@link BuildInfoAware<Status>}
     */
    Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Round round, Status status);

    /**
     * Update {@link BuildInfo}.
     *
     * @param buildInfo {@link BuildInfo} to update
     * @param status    {@link Status}
     * @return {@link BuildInfoAware<Status>}
     */
    Mono<BuildInfoAware<Status>> updateBuildInfo(BuildInfo buildInfo, Status status);

    /**
     * Find last state for {@link BuildInfo}.
     *
     * @param buildInfo {@link BuildInfo} to get last state
     * @return {@link BuildInfoAware<Status>}
     */
    Mono<BuildInfoAware<Status>> findBuildInfo(BuildInfo buildInfo);

    /**
     * Find all finished  {@link BuildInfo}s by period.
     *
     * @param from start of the period (inclusive)
     * @param to   end of the period (inclusive)
     * @return {@link BuildInfoAware<Round>}
     */
    Flux<BuildInfoAware<Round>> findAllFinishedBuildsByPeriod(Instant from, Instant to);

    /**
     * Find all finished  {@link BuildInfo}s for active round.
     *
     * @return {@link BuildInfoAware}
     */
    Flux<BuildInfoAware<Pair<Status, Round>>> findAllFromActiveRounds();
}
