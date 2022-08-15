package com.lllbllllb.productinfoservice;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import reactor.core.publisher.Flux;

public interface ProductInfoServiceRepositoryRemoteService {

    Flux<BuildInfo> getAllBuildInfo();

    Flux<BuildInfo> getBuildInfoByProductCode(String productCode);

}
