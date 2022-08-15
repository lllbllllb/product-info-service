package com.lllbllllb.productinfoservice.repositorylocal;

import java.util.UUID;

import com.lllbllllb.productinfoservice.repositorylocal.model.RoundDto;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProductInfoServiceRoundRepository extends R2dbcRepository<RoundDto, UUID> {

}
