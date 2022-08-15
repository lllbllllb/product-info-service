package com.lllbllllb.productinfoservice;

import java.time.Instant;
import java.util.List;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceBuildInfoRepositoryService {

    Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Round round, Status status);

    Mono<BuildInfoAware<Status>> updateBuildInfo(BuildInfo buildInfo, Status status);

    Flux<BuildInfoAware<Status>> findAllBuildInfo(List<BuildInfo> buildInfos);

    Flux<BuildInfoAware<Round>> findAllFinishedBuildsByPeriod(Instant from, Instant to);

    Flux<BuildInfoAware<Pair<Status, Round>>> findAllFromActiveRounds();
}
