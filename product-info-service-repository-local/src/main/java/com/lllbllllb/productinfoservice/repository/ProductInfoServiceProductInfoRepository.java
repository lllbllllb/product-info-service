package com.lllbllllb.productinfoservice.repository;

import java.util.UUID;

import com.lllbllllb.productinfoservice.repository.model.ProductInfoDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceProductInfoRepository extends R2dbcRepository<ProductInfoDto, UUID> {

    @Query("select distinct * from product_info pi join build_info bi on pi.build_info_id = bi.id where bi.product_code = $1")
    Flux<ProductInfoDto> findAllByProductCode(String productCode);

    @Query("select distinct * from product_info pi join build_info bi on pi.build_info_id = bi.id where bi.product_code = $1 and bi.full_number = $2")
    Mono<ProductInfoDto> findByProductCodeAndFullNumber(String productCode, String fullNumber);
}
