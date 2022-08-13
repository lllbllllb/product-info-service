package com.lllbllllb.productinfoservice.repository;

import java.util.UUID;

import com.lllbllllb.productinfoservice.repository.model.BuildInfoDto;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProductInfoServiceBuildInfoRepository extends R2dbcRepository<BuildInfoDto, UUID> {

}
