package com.lllbllllb.productinfoservice.repository;

import java.util.UUID;

import com.lllbllllb.productinfoservice.repository.model.RoundDto;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProductInfoServiceRoundRepository extends R2dbcRepository<RoundDto, UUID> {

}
