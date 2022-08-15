package com.lllbllllb.productinfoservice.repositorylocal;

import java.time.Instant;
import java.util.UUID;

import com.lllbllllb.productinfoservice.model.Status;
import com.lllbllllb.productinfoservice.repositorylocal.model.BuildInfoDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProductInfoServiceBuildInfoRepository extends R2dbcRepository<BuildInfoDto, UUID> {

    @Query("select distinct * from build_info bi join product_info pi on bi.id = pi.build_info_id where pi.last_modified_date between $1 and $2")
    Flux<BuildInfoDto> findAllFinishedByPeriod(Instant from, Instant to);

    Flux<BuildInfoDto> findAllByStatus(Status status);

    Flux<BuildInfoDto> findAllByRoundId(UUID roundId);
}
