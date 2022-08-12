package com.lllbllllb.productinfoservice.core.repository;

import java.util.List;
import java.util.UUID;

import com.lllbllllb.productinfoservice.core.repository.model.BuildInfoDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceRepository extends ReactiveMongoRepository<BuildInfoDto, String> {

    Flux<BuildInfoDto> findAllByProductCode(String productCode);

    Mono<BuildInfoDto> findByProductCodeAndFullNumber(String productCode, String fullNumber);

    Flux<BuildInfoDto> findAllById(List<UUID> ids);
}
