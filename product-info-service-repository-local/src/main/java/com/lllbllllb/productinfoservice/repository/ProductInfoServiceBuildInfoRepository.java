package com.lllbllllb.productinfoservice.repository;

import java.util.UUID;

import com.lllbllllb.productinfoservice.model.Status;
import com.lllbllllb.productinfoservice.repository.model.BuildInfoDto;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProductInfoServiceBuildInfoRepository extends R2dbcRepository<BuildInfoDto, UUID> {

    Flux<BuildInfoDto> findAllByStatus(Status status);

}
