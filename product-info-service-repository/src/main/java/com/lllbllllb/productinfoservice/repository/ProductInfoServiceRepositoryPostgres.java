package com.lllbllllb.productinfoservice.repository;

import java.util.List;
import java.util.UUID;

import com.lllbllllb.productinfoservice.repository.model.BuildInfoDto;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceRepositoryPostgres extends R2dbcRepository<BuildInfoDto, String> {

    Flux<BuildInfoDto> findAllByProductCode(String productCode);

    Mono<BuildInfoDto> findByProductCodeAndFullNumber(String productCode, String fullNumber);

    Flux<BuildInfoDto> findAllById(List<UUID> ids);
}
